//
//  PhotoEditor+Crop.swift
//  Pods
//
//  Created by Mohamed Hamed on 6/16/17.
//
//

import Foundation
import UIKit

// MARK: - CropView
extension PhotoEditorViewController: CropViewControllerDelegate {
    public func cropViewController(_ controller: CropViewController, didFinishCroppingImage image: UIImage, transform: CGAffineTransform, cropRect: CGRect) {
        controller.dismiss(animated: true, completion: nil)
        let imageView = UIImageView(image: image)
        let hSize = image.suitableSize(heightLimit: self.view.frame.height)
        let wSize = image.suitableSize( widthLimit: self.view.frame.width)
        if(hSize != nil && wSize != nil){
            let size = hSize!.height < wSize!.height ? hSize : wSize;
            imageView.frame.size = size!;
        }
        imageView.tag = CROP_IMAGE_VIEW_TAG
        imageView.center = self.view.center
        addLayer(layer: imageView)
        addCropImageIndex(index: lastActiveLayerIndex)
    }
    
    public func cropViewControllerDidCancel(_ controller: CropViewController) {
        controller.dismiss(animated: true, completion: nil)
    }
    
}
