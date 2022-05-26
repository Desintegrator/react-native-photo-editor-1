
//
//  CropRectView.swift
//  CropViewController
//
//  Created by Guilherme Moura on 2/26/16.
//  Copyright © 2016 Reefactor, Inc. All rights reserved.
// Credit https://github.com/sprint84/PhotoCropEditor

import UIKit

protocol CropRectViewDelegate: AnyObject {
    func cropRectViewDidBeginEditing(_ view: CropRectView)
    func cropRectViewDidChange(_ view: CropRectView)
    func cropRectViewDidEndEditing(_ view: CropRectView)
}

class CropRectView: UIView, ResizeControlDelegate {
    weak var delegate: CropRectViewDelegate?
    var showsGridMajor = true {
        didSet {
            setNeedsDisplay()
        }
    }
    var showsGridMinor = false {
        didSet {
            setNeedsDisplay()
        }
    }
    

    fileprivate var resizeImageView: UIImageView!
    fileprivate let topLeftCornerView = ResizeControl()
    fileprivate let topRightCornerView = ResizeControl()
    fileprivate let bottomLeftCornerView = ResizeControl()
    fileprivate let bottomRightCornerView = ResizeControl()
    fileprivate let topEdgeView = ResizeControl()
    fileprivate let leftEdgeView = ResizeControl()
    fileprivate let rightEdgeView = ResizeControl()
    fileprivate let bottomEdgeView = ResizeControl()
    fileprivate var initialRect = CGRect.zero
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        initialize()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        initialize()
    }
    
    fileprivate func initialize() {
        backgroundColor = UIColor.clear
        contentMode = .redraw
        
        resizeImageView = UIImageView(frame: bounds.insetBy(dx: -2.0, dy: -2.0))
        resizeImageView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        let bundle = Bundle(for: type(of: self))
        let image = UIImage(named: "PhotoCropEditorBorder", in: bundle, compatibleWith: nil)
        resizeImageView.image = image?.resizableImage(withCapInsets: UIEdgeInsets(top: 23.0, left: 23.0, bottom: 23.0, right: 23.0))
        addSubview(resizeImageView)
        
        topEdgeView.delegate = self
        addSubview(topEdgeView)
        leftEdgeView.delegate = self
        addSubview(leftEdgeView)
        rightEdgeView.delegate = self
        addSubview(rightEdgeView)
        bottomEdgeView.delegate = self
        addSubview(bottomEdgeView)
        
        topLeftCornerView.delegate = self
        addSubview(topLeftCornerView)
        topRightCornerView.delegate = self
        addSubview(topRightCornerView)
        bottomLeftCornerView.delegate = self
        addSubview(bottomLeftCornerView)
        bottomRightCornerView.delegate = self
        addSubview(bottomRightCornerView)
    }
    
    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        for subview in subviews where subview is ResizeControl {
            if subview.frame.contains(point) {
                return subview
            }
        }
        return nil
    }
  
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        topLeftCornerView.frame.origin = CGPoint(x: topLeftCornerView.bounds.width / -2.0, y: topLeftCornerView.bounds.height / -2.0)
        topRightCornerView.frame.origin = CGPoint(x: bounds.width - topRightCornerView.bounds.width - 2.0, y: topRightCornerView.bounds.height / -2.0)
        bottomLeftCornerView.frame.origin = CGPoint(x: bottomLeftCornerView.bounds.width / -2.0, y: bounds.height - bottomLeftCornerView.bounds.height / 2.0)
        bottomRightCornerView.frame.origin = CGPoint(x: bounds.width - bottomRightCornerView.bounds.width / 2.0, y: bounds.height - bottomRightCornerView.bounds.height / 2.0)
        
        topEdgeView.frame = CGRect(x: topLeftCornerView.frame.maxX, y: topEdgeView.frame.height / -2.0, width: topRightCornerView.frame.minX - topLeftCornerView.frame.maxX, height: topEdgeView.bounds.height)
        leftEdgeView.frame = CGRect(x: leftEdgeView.frame.width / -2.0, y: topLeftCornerView.frame.maxY, width: leftEdgeView.frame.width, height: bottomLeftCornerView.frame.minY - topLeftCornerView.frame.maxY)
        bottomEdgeView.frame = CGRect(x: bottomLeftCornerView.frame.maxX, y: bottomLeftCornerView.frame.minY, width: bottomRightCornerView.frame.minX - bottomLeftCornerView.frame.maxX, height: bottomEdgeView.frame.height)
        rightEdgeView.frame = CGRect(x: bounds.width - rightEdgeView.frame.width / 2.0, y: topRightCornerView.frame.maxY, width: rightEdgeView.frame.width, height: bottomRightCornerView.frame.minY - topRightCornerView.frame.maxY)
    }
    
    func enableResizing(_ enabled: Bool) {
        resizeImageView.isHidden = !enabled
        
        topLeftCornerView.enabled = enabled
        topRightCornerView.enabled = enabled
        bottomLeftCornerView.enabled = enabled
        bottomRightCornerView.enabled = enabled
        
        topEdgeView.enabled = enabled
        leftEdgeView.enabled = enabled
        bottomEdgeView.enabled = enabled
        rightEdgeView.enabled = enabled
    }

    // MARK: - ResizeControl delegate methods
    func resizeControlDidBeginResizing(_ control: ResizeControl) {
        initialRect = frame
        delegate?.cropRectViewDidBeginEditing(self)
    }
    
    func resizeControlDidResize(_ control: ResizeControl) {
        frame = cropRectWithResizeControlView(control)
        delegate?.cropRectViewDidChange(self)
    }
    
    func resizeControlDidEndResizing(_ control: ResizeControl) {
        delegate?.cropRectViewDidEndEditing(self)
    }
    
    fileprivate func cropRectWithResizeControlView(_ resizeControl: ResizeControl) -> CGRect {
        var rect = frame
        
        if resizeControl == topEdgeView {
            rect = CGRect(x: initialRect.minX,
                          y: initialRect.minY + resizeControl.translation.y,
                          width: initialRect.width,
                          height: initialRect.height - resizeControl.translation.y)
            
        } else if resizeControl == leftEdgeView {
            rect = CGRect(x: initialRect.minX + resizeControl.translation.x,
                          y: initialRect.minY,
                          width: initialRect.width - resizeControl.translation.x,
                          height: initialRect.height)
            
          
        } else if resizeControl == bottomEdgeView {
            rect = CGRect(x: initialRect.minX,
                          y: initialRect.minY,
                          width: initialRect.width,
                          height: initialRect.height + resizeControl.translation.y)
            
         
        } else if resizeControl == rightEdgeView {
            rect = CGRect(x: initialRect.minX,
                          y: initialRect.minY,
                          width: initialRect.width + resizeControl.translation.x,
                          height: initialRect.height)
            
        } else if resizeControl == topLeftCornerView {
            rect = CGRect(x: initialRect.minX + resizeControl.translation.x,
                          y: initialRect.minY + resizeControl.translation.y,
                          width: initialRect.width - resizeControl.translation.x,
                          height: initialRect.height - resizeControl.translation.y)
            

        } else if resizeControl == topRightCornerView {
            rect = CGRect(x: initialRect.minX,
                          y: initialRect.minY + resizeControl.translation.y,
                          width: initialRect.width + resizeControl.translation.x,
                          height: initialRect.height - resizeControl.translation.y)
            
            
        } else if resizeControl == bottomLeftCornerView {
            rect = CGRect(x: initialRect.minX + resizeControl.translation.x,
                          y: initialRect.minY,
                          width: initialRect.width - resizeControl.translation.x,
                          height: initialRect.height + resizeControl.translation.y)
            

        } else if resizeControl == bottomRightCornerView {
            rect = CGRect(x: initialRect.minX,
                          y: initialRect.minY,
                          width: initialRect.width + resizeControl.translation.x,
                          height: initialRect.height + resizeControl.translation.y)
            
         
        }
        
        let minWidth = leftEdgeView.bounds.width + rightEdgeView.bounds.width
        if rect.width < minWidth {
            rect.origin.x = frame.maxX - minWidth
            rect.size.width = minWidth
        }
        
        let minHeight = topEdgeView.bounds.height + bottomEdgeView.bounds.height
        if rect.height < minHeight {
            rect.origin.y = frame.maxY - minHeight
            rect.size.height = minHeight
        }
        
        return rect
    }
}
