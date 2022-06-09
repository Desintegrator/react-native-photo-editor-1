//
//  PhotoEditor+UITextView.swift
//  Pods
//
//  Created by Mohamed Hamed on 6/16/17.
//
//

import Foundation
import UIKit

extension PhotoEditorViewController: UITextViewDelegate {
    
    public func textViewDidChange(_ textView: UITextView) {
        let transform = textView.transform;
        textView.transform = .identity
        let oldFrame = textView.frame
        let sizeToFit = textView.sizeThatFits(CGSize(width: oldFrame.width, height:CGFloat.greatestFiniteMagnitude))
        textView.frame.size = CGSize(width: oldFrame.width, height: sizeToFit.height)
        textView.transform = transform
    }
    
    public func textViewDidBeginEditing(_ textView: UITextView) {
        isTyping = true
        activeTextView = textView
        textView.superview?.bringSubviewToFront(textView)
    }
    
    public func textViewDidEndEditing(_ textView: UITextView) {
        isTyping = false
        activeTextView = nil
    }
    
}
