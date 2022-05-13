//
//  PhotoEditor+StickersViewController.swift
//  Pods
//
//  Created by Mohamed Hamed on 6/16/17.
//
//

import Foundation
import UIKit

extension PhotoEditorViewController {
    
    
   
}

extension PhotoEditorViewController: StickersViewControllerDelegate {
    
    func didSelectView(view: UIView) {
        view.center = canvasImageView.center
        self.canvasImageView.addSubview(view)
        //Gestures
        addGestures(view: view)
    }
    
    func didSelectImage(image: UIImage) {        
        let imageView = UIImageView(image: image)
        imageView.contentMode = .scaleAspectFit
        imageView.frame.size = CGSize(width: 150, height: 150)
        imageView.center = canvasImageView.center
        
        self.canvasImageView.addSubview(imageView)
        //Gestures
        addGestures(view: imageView)
    }
    
    func stickersViewDidDisappear() {
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
