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
    @IBOutlet weak var imageViewHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var imageViewWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var canvasHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var canvasWidthConstraint: NSLayoutConstraint!
    //To hold the drawings
    @IBOutlet weak var canvasImageView: UIImageView!
    
    //Controls
    @objc public var image: UIImage?
    
    /**
     Array of Colors that will show while drawing or typing
     */
    @objc public var colors  : [UIColor] = []
    
    @objc public var photoEditorDelegate: PhotoEditorDelegate?
    
    // list of controls to be hidden
    @objc public var hiddenControls : [NSString] = []
    
    var drawColor: UIColor = UIColor.black
    var textColor: UIColor = UIColor.white
    var isDrawing: Bool = true
    var lastPoint: CGPoint!
    var swiped = false
    var lastPanPoint: CGPoint?
    var lastTextViewTransform: CGAffineTransform?
    var lastTextViewTransCenter: CGPoint?
    var lastTextViewFont:UIFont?
    var activeTextView: UITextView?
    var imageViewToPan: UIImageView?
    var isTyping: Bool = false
    
    //Register Custom font before we load XIB
    public override func loadView() {
        registerFont()
        super.loadView()
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
//        if(image != nil){
//            self.setImageView(image: image!)
//        }
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
}

extension PhotoEditorViewController: ColorDelegate {
    func didSelectColor(color: UIColor) {
        
        self.drawColor = color
        if activeTextView != nil {
            activeTextView?.textColor = color
            textColor = color
        }
    }
}
