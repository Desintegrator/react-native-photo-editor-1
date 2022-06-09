//
//  PhotoEditor+Gestures.swift
//  Photo Editor
//
//  Created by Mohamed Hamed on 6/16/17.
//
//

import Foundation


import UIKit

extension PhotoEditorViewController : UIGestureRecognizerDelegate  {
    
    /**
     UIPanGestureRecognizer - Moving Objects
     Selecting transparent parts of the imageview won't move the object
     */
    @objc func panGesture(_ recognizer: UIPanGestureRecognizer) {
        if let view = recognizer.view {
            if view is UIImageView {
                //Tap only on visible parts on the image
                if recognizer.state == .began {
                    for imageView in subImageViews(view: self.canvasImageView) {
                        let location = recognizer.location(in: imageView)
                        let alpha = imageView.alphaAtPoint(location)
                        if alpha > 0 {
                            imageViewToPan = imageView
                            break
                        }
                    }
                }
                if imageViewToPan != nil {
                    moveView(view: imageViewToPan!, recognizer: recognizer)
                }
            } else {
                moveView(view: view, recognizer: recognizer)
            }
        }
    }
    
    /**
     UIPinchGestureRecognizer - Pinching Objects
     If it's a UITextView will make the font bigger so it doen't look pixlated
     */
    @objc func pinchGesture(_ recognizer: UIPinchGestureRecognizer) {
        if let view = recognizer.view {
            if view is UITextView {
//                let textView = view as! UITextView
//
//                if textView.font!.pointSize * recognizer.scale < 90 {
//                    let font = UIFont(name: textView.font!.fontName, size: textView.font!.pointSize * recognizer.scale)
//                    textView.font = font
//                    let sizeToFit = textView.sizeThatFits(CGSize(width: UIScreen.main.bounds.size.width,
//                                                                 height:CGFloat.greatestFiniteMagnitude))
//                    textView.bounds.size = CGSize(width: textView.intrinsicContentSize.width,
//                                                  height: sizeToFit.height)
//                } else {
//                    let sizeToFit = textView.sizeThatFits(CGSize(width: UIScreen.main.bounds.size.width,
//                                                                 height:CGFloat.greatestFiniteMagnitude))
//                    textView.bounds.size = CGSize(width: textView.intrinsicContentSize.width,
//                                                  height: sizeToFit.height)
//                }
//
//
//                textView.setNeedsDisplay()
            } else {
                view.transform = view.transform.scaledBy(x: recognizer.scale, y: recognizer.scale)
            }
            recognizer.scale = 1
        }
    }
    
    /**
     UIRotationGestureRecognizer - Rotating Objects
     */
    @objc func rotationGesture(_ recognizer: UIRotationGestureRecognizer) {
        if let view = recognizer.view {
            view.transform = view.transform.rotated(by: recognizer.rotation)
            recognizer.rotation = 0
        }
    }
    
    /**
     UITapGestureRecognizer - Taping on Objects
     Will make scale scale Effect
     Selecting transparent parts of the imageview won't move the object
     */
    @objc func tapGesture(_ recognizer: UITapGestureRecognizer) {
        if let view = recognizer.view {
            if view is UIImageView {
                //Tap only on visible parts on the image
                for imageView in subImageViews(view: canvasImageView) {
                    let location = recognizer.location(in: imageView)
                    let alpha = imageView.alphaAtPoint(location)
                    if alpha > 0 {
                        scaleEffect(view: imageView)
                        break
                    }
                }
            } else {
                scaleEffect(view: view)
                if(view is UITextView){
                    view.becomeFirstResponder()
                    activeTextView = view as? UITextView
                }
            }
        }
    }
    
    /*
     Support Multiple Gesture at the same time
     */
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return true
    }
    
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRequireFailureOf otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return false
    }
    
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldBeRequiredToFailBy otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return false
    }
    
    @objc func screenEdgeSwiped(_ recognizer: UIScreenEdgePanGestureRecognizer) {
        
    }
    
    // to Override Control Center screen edge pan from bottom
    override public var prefersStatusBarHidden: Bool {
        return true
    }
    
    /**
     Scale Effect
     */
    func scaleEffect(view: UIView) {
        view.superview?.bringSubviewToFront(view)
        
        if #available(iOS 10.0, *) {
            let generator = UIImpactFeedbackGenerator(style: .heavy)
            generator.impactOccurred()
        }
        let previouTransform =  view.transform
        UIView.animate(withDuration: 0.2,
                       animations: {
                        view.transform = view.transform.scaledBy(x: 1.2, y: 1.2)
        },
                       completion: { _ in
                        UIView.animate(withDuration: 0.2) {
                            view.transform  = previouTransform
                        }
        })
    }
    
    /**
     Moving Objects 
     delete the view if it's inside the delete view
     Snap the view back if it's out of the canvas
     */

    func moveView(view: UIView, recognizer: UIPanGestureRecognizer)  {        
        view.superview?.bringSubviewToFront(view)

        view.center = CGPoint(x: view.center.x + recognizer.translation(in: firstActiveLayer).x,
                              y: view.center.y + recognizer.translation(in: firstActiveLayer).y)

        recognizer.setTranslation(CGPoint.zero, in: firstActiveLayer)
//
        if recognizer.state == .ended {
            imageViewToPan = nil
            if !firstActiveLayer.frame.contains(view.center) {
                UIView.animate(withDuration: 0.3, animations: {
                    view.center = self.firstActiveLayer.center
                })
            }
        }
    }
    
    func subImageViews(view: UIView) -> [UIImageView] {
        var imageviews: [UIImageView] = []
        for imageView in view.subviews {
            if imageView is UIImageView {
                imageviews.append(imageView as! UIImageView)
            }
        }
        return imageviews
    }
    
    
    func addGestures(view: UIView) {
        //Gestures
        view.isUserInteractionEnabled = true
        
        let panGesture = UIPanGestureRecognizer(target: self,
                                                action: #selector(PhotoEditorViewController.panGesture))
        panGesture.minimumNumberOfTouches = 1
        panGesture.maximumNumberOfTouches = 1
        panGesture.delegate = self
        view.addGestureRecognizer(panGesture)
        
        let pinchGesture = UIPinchGestureRecognizer(target: self,
                                                    action: #selector(PhotoEditorViewController.pinchGesture))
        pinchGesture.delegate = self
        view.addGestureRecognizer(pinchGesture)
        
        let rotationGestureRecognizer = UIRotationGestureRecognizer(target: self,
                                                                    action:#selector(PhotoEditorViewController.rotationGesture) )
        rotationGestureRecognizer.delegate = self
        view.addGestureRecognizer(rotationGestureRecognizer)
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(PhotoEditorViewController.tapGesture))
        view.addGestureRecognizer(tapGesture)
        
    }
}
