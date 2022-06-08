import Foundation
import UIKit
import SDWebImage

@objc
class RNPhotoEditorView: UIView {
    var photoEditor: PhotoEditorViewController!;
    var cropController: CropViewController?;
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        photoEditor = PhotoEditorViewController(nibName:"PhotoEditorViewController",bundle: Bundle(for: PhotoEditorViewController.self));

        photoEditor.onLayersUpdate = _onLayersUpdate;
        photoEditor.onPhotoProcessed = _onPhotoProcessed;

        let photoEditorView = photoEditor.view;
        photoEditorView?.frame = self.frame;
        photoEditorView?.bounds = self.bounds;
        photoEditorView?.autoresizingMask = [.flexibleHeight,.flexibleWidth]
        let emptyImage = UIColor.white.image(CGSize(width: 512, height: 256))
        photoEditor.setImageView(image: emptyImage);
        addSubview(photoEditorView!);
        setupView()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView() {
        photoEditor.loadViewIfNeeded()
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc var mode: NSString = "none" {
        didSet {
            photoEditor.mode = mode;
            updatePhotoEditor();
        }
    }
    
    func updatePhotoEditor(){
        let mode = photoEditor.mode;
        photoEditor.isDrawing = ["pencil","marker","square"].contains(mode);
        photoEditor.drawMode = mode;
        if(mode == "crop"){
            addСropController()
        }else{
            removeСropController()
        }
    }
    func addСropController() {
        if(photoEditor.image != nil){
            let controller = CropViewController()
            controller.delegate = photoEditor;
            let croppedImage = photoEditor.generateImage();
            controller.image = croppedImage;
//            photoEditor.canvasImageView.addSubview(controller.view);
            addSubview(controller.view)
//            photoEditor.addChild(controller);
//            controller.view?.frame = photoEditor.imageView.bounds;
            controller.view.frame = frame;
            controller.view.bounds = bounds;
            photoEditor.hideLayers();
            photoEditor.imageView.isHidden = true;
            controller.resetCropRect();
            cropController = controller;
            controller.didMove(toParent: photoEditor);
        }
    }


    func submitСropController() {
        cropController?.done();
        removeСropController();
    }

    func removeСropController() {
        if cropController != nil {
        if subviews.contains(cropController!.view) {
                cropController!.view.removeFromSuperview();
            }
        }
        photoEditor.imageView.isHidden = false;
        photoEditor.showLayers();
    }

    @objc var toolSize: CGFloat = 50.0 {
        didSet {
            photoEditor.toolSize = self.toolSize;
            self.setupView()
        }
    }

    @objc var toolColor = UIColor.black {
        didSet {
            photoEditor.toolColor = self.toolColor;
            self.setupView()
        }
    }

    @objc var onLayersUpdate: RCTDirectEventBlock?
    @objc var onPhotoProcessed: RCTDirectEventBlock?

    func _onLayersUpdate() {
      if (self.onLayersUpdate != nil) {
        // print("TEST=> _onLayersUpdate")
        self.onLayersUpdate!([
          "layersCount": photoEditor.layers.count,
          "activeLayer": photoEditor.activeLayerNumber
        ]);
      }
    }

    func _onPhotoProcessed(path: String) {
      if (self.onLayersUpdate != nil) {
        // print("TEST=> _onPhotoProcessed")
        self.onPhotoProcessed!([
          "path": path,
        ]);
      }
    }

    @objc var onImageLoadError: RCTDirectEventBlock?

    @objc
    func setSource(source:NSDictionary){
        let url:URL = URL(string: source["uri"] as! String)!;
        let headers: NSDictionary = source["headers"] as! NSDictionary;

        let requestModifier = SDWebImageDownloaderRequestModifier { (request) -> URLRequest? in
            if(request.url != nil){
                var mutableRequest = request;
                for (key, value) in headers {
                    mutableRequest.addValue(value as! String, forHTTPHeaderField: key as! String);
                }
                return mutableRequest;
            }
            return request
        };
        SDWebImageDownloader.shared.requestModifier = requestModifier;
        SDWebImageDownloader.shared.downloadImage(with: url) { image, data, error, finished in
            if(image != nil && finished){
                self.photoEditor.image = image;
                self.photoEditor.setImageView(image: image!);
                self.updatePhotoEditor();
            } else {
                if(self.onImageLoadError != nil){
                    let errorMsg = "Failed to load image: \(String(describing: error))";
                  self.onImageLoadError!(["error": errorMsg]);
                  }
            }
        }
    }

    @objc
    func clearAll() {
        photoEditor.clearAll()
    }

    @objc
    func crop() {
        submitСropController();
    }

    @objc
    func rotate(clockwise: Bool) {
        cropController?.cropView?.rotateImage(rotationAngle: (clockwise == true ? .pi/2:-.pi/2));
    }

    @objc
    func redo() {
        photoEditor.redo()
    }

    @objc
    func undo() {
        photoEditor.undo()
    }

    @objc
    func reload() {
        photoEditor.reload()
    }

    @objc
    func processPhoto() {
        photoEditor.processPhoto()
    }
}
