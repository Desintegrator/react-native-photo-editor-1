//
//  ViewController.swift
//  Photo Editor
//
//  Created by Mohamed Hamed on 4/23/17.
//  Copyright © 2017 Mohamed Hamed. All rights reserved.
//

import UIKit

extension UIColor {
    func image(_ size: CGSize = CGSize(width: 100, height: 100)) -> UIImage {
        return UIGraphicsImageRenderer(size: size).image { rendererContext in
            self.setFill()
            rendererContext.fill(CGRect(origin: .zero, size: size))
        }
    }
}

public final class PhotoEditorViewController: UIViewController {

    /** holding the 2 imageViews original image and drawing & stickers */
    @IBOutlet weak var canvasView: UIView!
    //To hold the image
    @IBOutlet var imageView: UIImageView!
//    @IBOutlet var drawImageView: UIImageView!

    @IBOutlet weak var imageViewHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var imageViewWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var canvasHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var canvasWidthConstraint: NSLayoutConstraint!
    //To hold the drawings
    @IBOutlet weak var canvasImageView: UIImageView!

    @objc public var image: UIImage?

    @objc public var photoEditorDelegate: PhotoEditorDelegate?
    
    let CROP_IMAGE_VIEW_TAG = 8000
    
    var toolSize: CGFloat = 50.0
    var toolColor: UIColor = UIColor.black
    var textColor: UIColor = UIColor.white
    var isDrawing: Bool = true
    var mode: NSString = "pencil"
    var drawMode: NSString = "pencil"
    var lastPoint: CGPoint!
    var firstPoint: CGPoint!
    var swiped = false
    var lastPanPoint: CGPoint?
    var lastTextViewTransform: CGAffineTransform?
    var lastTextViewTransCenter: CGPoint?
    var lastTextViewFont:UIFont?
    var activeTextView: UITextView?
    var imageViewToPan: UIImageView?
    var isTyping: Bool = false
    var textExists: Bool = false

    var onLayersUpdate: (() -> ())? = nil
    var onPhotoProcessed: ((String) -> ())? = nil

    var layers: [UIView] = []
    var cropImagesLayersIndexes: [Int] = []
    var lastActiveLayerIndex = -1;

    
    var firstActiveLayerIndex: Int {
        get {
            if(cropImagesLayersIndexes.isEmpty){
                return -1;
            }
            return cropImagesLayersIndexes.last!;
        }
    }
    
    var firstActiveLayer: UIImageView {
        get {
            if(firstActiveLayerIndex == -1) {return imageView}
            return layers[firstActiveLayerIndex] as! UIImageView
        }
    }
    
    //Register Custom font before we load XIB
    public override func loadView() {
        super.loadView()
    }

    override public func viewDidLoad() {
        super.viewDidLoad()

        let edgePan = UIScreenEdgePanGestureRecognizer(target: self, action: #selector(screenEdgeSwiped))
        //        edgePan.edges = .bottom
        edgePan.delegate = self
        self.view.addGestureRecognizer(edgePan)

        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow),
                                              name: UIResponder.keyboardDidShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide),
                                              name: UIResponder.keyboardWillHideNotification, object: nil)
        NotificationCenter.default.addObserver(self,selector: #selector(keyboardWillChangeFrame(_:)),
                                              name: UIResponder.keyboardWillChangeFrameNotification, object: nil)


    }


    func setImageView(image: UIImage) {
        imageView.image = image
        let hSize = image.suitableSize(heightLimit: self.view.frame.height)
        let wSize = image.suitableSize( widthLimit: self.view.frame.width)
        if(hSize != nil && wSize != nil){
            let size = hSize!.height<wSize!.height ? hSize:wSize;
            imageViewHeightConstraint.constant = (size?.height)!
            imageViewWidthConstraint.constant = (size?.width)!
        }
    }
    

    func reload() {
        if(self.onLayersUpdate != nil){
            self.onLayersUpdate!();
        }
    }

    func processPhoto() {
        let path = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0].appendingPathComponent("examplePng.png")
        
        let pngData = self.generateImage().pngData()

        // print("path=> ", path)
        let url = path
        if (pngData != nil && url != nil) {
            do {
              try? pngData!.write(to: url, options: .atomic)
            } catch {
              self.onPhotoProcessed!("error")
            }
        }
        
        if(self.onPhotoProcessed != nil) {
          do {
            try self.onPhotoProcessed!("\(path)");
          } catch {
            self.onPhotoProcessed!("error")
          }
        }
    }


    func clearAll() {
        let newImageView = UIImageView(image: self.image)
        newImageView.frame = self.imageView.frame
        newImageView.tag = CROP_IMAGE_VIEW_TAG
        newImageView.center = self.view.center
        addLayer(layer: newImageView)
        addCropImageIndex(index: lastActiveLayerIndex)
        updateLayersVisibility()
    }
    
    func addLayer(layer: UIView) {
        if(layers.count - 1 > lastActiveLayerIndex){
            while(layers.count - 1 > lastActiveLayerIndex){
                layers.popLast()?.removeFromSuperview();
            }
            lastActiveLayerIndex = layers.count - 1
        }
        layers.append(layer)
        if(layer is UITextView){
            canvasImageView.addSubview(layer)
            textExists = true;
        }else{
            canvasView.addSubview(layer)
        }
        view.bringSubviewToFront(canvasImageView)
        self.lastActiveLayerIndex += 1
        updateLayersVisibility()
    }
    
    func saveTextLayers(){
        if(activeTextView != nil){
            activeTextView!.endEditing(true)
        }
        if(textExists){
            for (index, layer) in layers.enumerated() {
                if(layer is UITextView && !layer.isHidden){
                    let convertedCenter = canvasView.convert(layer.center, to: self.firstActiveLayer);
                    let imageView = UIImageView(frame: self.firstActiveLayer.frame);
                    layer.removeFromSuperview();
                    imageView.addSubview(layer);
                    layer.center = convertedCenter;
                    layers[index] = imageView;
                    canvasView.addSubview(imageView)
                }
            }
            textExists = false
        }
    }
    
    func addCropImageIndex(index: Int){
        cropImagesLayersIndexes.append(index)
        updateLayersVisibility()
    }
    
    func removeLastCropImageIndex(){
        cropImagesLayersIndexes.removeLast();
        updateLayersVisibility()
    }
    
    func updateLayersVisibility(){
        if(self.onLayersUpdate != nil){
            self.onLayersUpdate!();
        }
        self.imageView.isHidden = firstActiveLayerIndex >= 0;
        for (index, layer) in layers.enumerated() {
            layer.isHidden = index < firstActiveLayerIndex || index > lastActiveLayerIndex
        }    
    }
    
    func removeLayerByIndex(index: Int){
        let layer = layers[index];
        layer.removeFromSuperview();
        layers.remove(at: index)
        if(self.lastActiveLayerIndex >= layers.count) {
            self.lastActiveLayerIndex = layers.count - 1
        }
    }
    func hideLayers(){
        imageView.isHidden = true;
        layers.forEach {
            layer in
            layer.isHidden = true
        }
    }
    
    func undo(){
        if(lastActiveLayerIndex >= 0){
            lastActiveLayerIndex -= 1;
            if(lastActiveLayerIndex < firstActiveLayerIndex){
                removeLastCropImageIndex()
            }
            updateLayersVisibility()
        }
    }
    
    func redo(){
        if(lastActiveLayerIndex < layers.count - 1){
            lastActiveLayerIndex += 1;
            if(layers[lastActiveLayerIndex].tag == CROP_IMAGE_VIEW_TAG){
                addCropImageIndex(index: lastActiveLayerIndex)
            }
            updateLayersVisibility()
        }
    }
    
    func generateImage()-> UIImage {
        let size = self.firstActiveLayer.frame.integral.size
        let areaSize = CGRect(x: 0, y: 0, width: size.width, height: size.height)

        UIGraphicsBeginImageContextWithOptions(size, false, 0);
        self.firstActiveLayer.image!.draw(in: areaSize);
        layers.forEach {
            layer in if(!layer.isHidden){
                layer.drawHierarchy(in: areaSize, afterScreenUpdates: true);
            }
            
        }
        return UIGraphicsGetImageFromCurrentImageContext()!;
    }
}

extension PhotoEditorViewController: ColorDelegate {
    func didSelectColor(color: UIColor) {

        self.toolColor = color
        if activeTextView != nil {
            activeTextView?.textColor = color
            textColor = color
        }
    }
}
