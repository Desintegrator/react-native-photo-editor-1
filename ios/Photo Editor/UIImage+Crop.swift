//
//  UIImage+Crop.swift
//  CropViewController
//
//  Created by Guilherme Moura on 2/26/16.
//  Copyright © 2016 Reefactor, Inc. All rights reserved.
// Credit https://github.com/sprint84/PhotoCropEditor

import UIKit

extension UIImage {
    func cropImage(rect: CGRect) -> UIImage? {
        let cropRect = rect.applying(CGAffineTransform(scaleX: self.scale, y: self.scale))
        
        guard let croppedImage = self.cgImage?.cropping(to: cropRect) else { return nil }
        let image = UIImage(cgImage: croppedImage, scale: self.scale, orientation: self.imageOrientation)
        
        return image
    }
    
    func rotate(rotationAngle: CGFloat) -> UIImage? {
        let newSize = CGRect(origin: CGPoint.zero, size: self.size).applying(CGAffineTransform(rotationAngle: rotationAngle)).size

        UIGraphicsBeginImageContextWithOptions(newSize, false, self.scale)
        let context = UIGraphicsGetCurrentContext()!
        context.translateBy(x: newSize.width/2, y: newSize.height/2)
        context.rotate(by: rotationAngle)
        self.draw(in: CGRect(x: -self.size.width/2, y: -self.size.height/2, width: self.size.width, height: self.size.height))

        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newImage
    }
}
