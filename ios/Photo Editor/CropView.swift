//
//  CropView.swift
//  CropViewController
//
//  Created by Guilherme Moura on 2/25/16.
//  Copyright © 2016 Reefactor, Inc. All rights reserved.
// Credit https://github.com/sprint84/PhotoCropEditor

import UIKit
import AVFoundation

open class CropView: UIView, UIGestureRecognizerDelegate, CropRectViewDelegate {
    open var image: UIImage? {
        didSet {
            if image != nil {
                imageSize = image!.size
            }
            imageView?.removeFromSuperview()
            imageView = nil
            setNeedsLayout()
        }
    }
    open var imageView: UIImageView? {
        didSet {
            if let view = imageView , image == nil {
                imageSize = view.frame.size
            }
            usingCustomImageView = true
            setNeedsLayout()
        }
    }
    open var croppedImage: UIImage? {
        return image?.cropImage(rect: zoomedCropRect())
    }
   
    open var cropAspectRatio: CGFloat {
        set {
            setCropAspectRatio(newValue, shouldCenter: true)
        }
        get {
            let rect = rootView.frame
            let width = rect.width
            let height = rect.height
            return width / height
        }
    }
    open var rotation: CGAffineTransform {
        guard let imgView = imageView else {
            return CGAffineTransform.identity
        }
        return imgView.transform
    }

    open var cropRect: CGRect {
        set {
        }
        get {
            return rootView.frame
        }
    }
    open var imageCropRect = CGRect.zero {
        didSet {
            resetCropRect()
            
            let scale = min(rootView.frame.width / imageSize.width, rootView.frame.height / imageSize.height)
            let x = imageCropRect.minX * scale + rootView.frame.minX
            let y = imageCropRect.minY * scale + rootView.frame.minY
            let width = imageCropRect.width * scale
            let height = imageCropRect.height * scale
            
            let rect = CGRect(x: x, y: y, width: width, height: height)
            let intersection = rect.intersection(rootView.frame)
            
            if !intersection.isNull {
                cropRect = intersection
            }
        }
    }
    func rotateImage(rotationAngle: CGFloat) {
        let rotatedImage = image?.rotate(rotationAngle: rotationAngle)
        if(rotatedImage != nil){
            self.image = rotatedImage;
        }
    }
    
    open var resizeEnabled = true {
        didSet {
            cropRectView.enableResizing(resizeEnabled)
        }
    }
    open var showCroppedArea = true {
        didSet {
            layoutIfNeeded()
            rootView.clipsToBounds = !showCroppedArea
            showOverlayView(showCroppedArea)
        }
    }
    fileprivate var imageSize = CGSize(width: 1.0, height: 1.0)
    fileprivate var rootView: UIView!
    fileprivate let cropRectView = CropRectView()
    fileprivate let topOverlayView = UIView()
    fileprivate let leftOverlayView = UIView()
    fileprivate let rightOverlayView = UIView()
    fileprivate let bottomOverlayView = UIView()
    fileprivate var insetRect = CGRect.zero
    fileprivate var editingRect = CGRect.zero
    fileprivate var interfaceOrientation = UIApplication.shared.statusBarOrientation
    fileprivate var resizing = false
    fileprivate var usingCustomImageView = false
    fileprivate let MarginTop: CGFloat = 0.0
    fileprivate let MarginLeft: CGFloat = 0.0

    public override init(frame: CGRect) {
        super.init(frame: frame)
        initialize()
    }
    
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        initialize()
    }

    fileprivate func initialize() {
        autoresizingMask = [.flexibleWidth, .flexibleHeight]
        backgroundColor = UIColor.clear
        
        rootView = UIView(frame: bounds)
        rootView.autoresizingMask = [.flexibleTopMargin, .flexibleLeftMargin, .flexibleBottomMargin, .flexibleRightMargin]
        rootView.backgroundColor = UIColor.clear
        addSubview(rootView)
        
        cropRectView.delegate = self
        addSubview(cropRectView)
        
        showOverlayView(showCroppedArea)
        addSubview(topOverlayView)
        addSubview(leftOverlayView)
        addSubview(rightOverlayView)
        addSubview(bottomOverlayView)
    }
    
    open override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        if !isUserInteractionEnabled {
            return nil
        }
        
        if let hitView = cropRectView.hitTest(convert(point, to: cropRectView), with: event) {
            return hitView
        }

        return super.hitTest(point, with: event)
    }
    
    open override func layoutSubviews() {
        super.layoutSubviews()
        let interfaceOrientation = UIApplication.shared.statusBarOrientation
        
        if image == nil && imageView == nil {
            return
        }
        
        setupEditingRect()

        if imageView == nil {
            if interfaceOrientation.isPortrait {
                insetRect = bounds.insetBy(dx: MarginLeft, dy: MarginTop)
            } else {
                insetRect = bounds.insetBy(dx: MarginLeft, dy: MarginLeft)
            }
            if !showCroppedArea {
                insetRect = editingRect
            }
            setupRootView()
            setupImageView()
        } else if usingCustomImageView {
            if interfaceOrientation.isPortrait {
                insetRect = bounds.insetBy(dx: MarginLeft, dy: MarginTop)
            } else {
                insetRect = bounds.insetBy(dx: MarginLeft, dy: MarginLeft)
            }
            if !showCroppedArea {
                insetRect = editingRect
            }
            setupRootView()
            imageView?.frame = rootView!.bounds
            rootView?.addSubview(imageView!)
            usingCustomImageView = false
        }
        
        if !resizing {
            layoutCropRectViewWithCropRect(rootView.frame)
        }
        
        
        self.interfaceOrientation = interfaceOrientation
    }
    
    
    open func resetCropRect() {
        imageView?.transform = CGAffineTransform.identity
        layoutCropRectViewWithCropRect(rootView.bounds)
    }
    
    
    open func zoomedCropRect() -> CGRect {
        let cropRect = convert(cropRectView.frame, to: rootView)
        var ratio: CGFloat = 1.0
       let orientation = UIApplication.shared.statusBarOrientation
       if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiom.pad || orientation.isPortrait) {
           ratio = AVMakeRect(aspectRatio: imageSize, insideRect: insetRect).width / imageSize.width
       } else {
           ratio = AVMakeRect(aspectRatio: imageSize, insideRect: insetRect).height / imageSize.height
       }
        let zoomedCropRect = CGRect(x: (cropRect.origin.x-(imageView?.frame.origin.x ?? 0)) / ratio,
                   y: (cropRect.origin.y-(imageView?.frame.origin.y ?? 0)) / ratio,
                   width: cropRect.size.width / ratio,
                   height: cropRect.size.height / ratio)
        return zoomedCropRect
    }
    
    open func croppedImage(_ image: UIImage) -> UIImage? {
        return image.cropImage(rect: zoomedCropRect())
    }
    
    // MARK: - Private methods
    fileprivate func showOverlayView(_ show: Bool) {
        let color = show ? UIColor(white: 0.0, alpha: 0.4) : UIColor.clear
        
        topOverlayView.backgroundColor = color
        leftOverlayView.backgroundColor = color
        rightOverlayView.backgroundColor = color
        bottomOverlayView.backgroundColor = color
    }
    
    fileprivate func setupEditingRect() {
        let interfaceOrientation = UIApplication.shared.statusBarOrientation
        if interfaceOrientation.isPortrait {
            editingRect = bounds.insetBy(dx: MarginLeft, dy: MarginTop)
        } else {
            editingRect = bounds.insetBy(dx: MarginLeft, dy: MarginLeft)
        }
        if !showCroppedArea {
            editingRect = CGRect(x: 0, y: 0, width: bounds.width, height: bounds.height)
        }
    }
    
    fileprivate func setupRootView() {
        let cropRect = AVMakeRect(aspectRatio: imageSize, insideRect: insetRect)
        rootView.frame = cropRect
        rootView.bounds = cropRect
    }

    fileprivate func setupImageView() {
        let imageView = UIImageView(frame: rootView!.bounds)
        imageView.backgroundColor = .clear
        imageView.contentMode = .scaleAspectFit
        imageView.image = image

        rootView?.addSubview(imageView)
        self.imageView = imageView
        usingCustomImageView = false
    }
    
    fileprivate func layoutCropRectViewWithCropRect(_ cropRect: CGRect) {
        cropRectView.frame = cropRect
        layoutOverlayViewsWithCropRect(cropRect)
    }
    
    fileprivate func layoutOverlayViewsWithCropRect(_ cropRect: CGRect) {
        topOverlayView.frame = CGRect(x: 0, y: 0, width: bounds.width, height: cropRect.minY)
        leftOverlayView.frame = CGRect(x: 0, y: cropRect.minY, width: cropRect.minX, height: cropRect.height)
        rightOverlayView.frame = CGRect(x: cropRect.maxX, y: cropRect.minY, width: bounds.width - cropRect.maxX, height: cropRect.height)
        bottomOverlayView.frame = CGRect(x: 0, y: cropRect.maxY, width: bounds.width, height: bounds.height - cropRect.maxY)
    }
   
    fileprivate func cappedCropRectInImageRectWithCropRectView(_ cropRectView: CropRectView) -> CGRect {
        var cropRect = cropRectView.frame
        let rect = convert(cropRect, to: rootView)
        if rect.minX < imageView!.frame.minX {
            cropRect.origin.x = rootView.convert(imageView!.frame, to: self).minX
            let xDiff = imageView!.frame.minX - rect.minX;
            let cappedWidth = rect.size.width - xDiff
            let maxWidth = rootView.convert(imageView!.frame, to: self).maxX - cropRect.minX
            let width = cappedWidth > maxWidth ? maxWidth : cappedWidth;
            let height =  cropRect.size.height
            cropRect.size = CGSize(width: width, height: height)
        }
        
        if rect.minY < imageView!.frame.minY {
            cropRect.origin.y = rootView.convert(imageView!.frame, to: self).minY
            let yDiff = imageView!.frame.minY - rect.minY;
            let cappedHeight = rect.size.height - yDiff;
            let maxHeight = rootView.convert(imageView!.frame, to: self).maxY - cropRect.minY
            let height = cappedHeight > maxHeight ? maxHeight : cappedHeight;
            let width = cropRect.size.width
            cropRect.size = CGSize(width: width, height: height)
        }
        
        if rect.maxX > imageView!.frame.maxX {
            let cappedWidth = rootView.convert(imageView!.frame, to: self).maxX - cropRect.minX
            let height =  cropRect.size.height
            cropRect.size = CGSize(width: cappedWidth, height: height)
        }
        
        if rect.maxY > imageView!.frame.maxY {
            let cappedHeight = rootView.convert(imageView!.frame, to: self).maxY - cropRect.minY
            let width =  cropRect.size.width
            cropRect.size = CGSize(width: width, height: cappedHeight)
        }
        
        return cropRect
    }
    

    
    fileprivate func setCropAspectRatio(_ ratio: CGFloat, shouldCenter: Bool) {
        var cropRect = rootView.frame
        var width = cropRect.width
        var height = cropRect.height
        if ratio <= 1.0 {
            width = height * ratio
            if width > imageView!.bounds.width {
                width = cropRect.width
                height = width / ratio
            }
        } else {
            height = width / ratio
            if height > imageView!.bounds.height {
                height = cropRect.height
                width = height * ratio
            }
        }
        cropRect.size = CGSize(width: width, height: height)
    }
    
    // MARK: - CropView delegate methods
    func cropRectViewDidBeginEditing(_ view: CropRectView) {
        resizing = true
    }
    
    func cropRectViewDidChange(_ view: CropRectView) {
        let cropRect = cappedCropRectInImageRectWithCropRectView(view)
        layoutCropRectViewWithCropRect(cropRect)
    }
    
    func cropRectViewDidEndEditing(_ view: CropRectView) {
        resizing = false
    }
    
    // MARK: - Gesture Recognizer delegate methods
    open func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return true
    }
}
