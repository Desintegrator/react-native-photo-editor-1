//
//  PhotoEditor+Drawing.swift
//  Photo Editor
//
//  Created by Mohamed Hamed on 6/16/17.
//
//

import UIKit

extension PhotoEditorViewController {
    var drawImageView: UIImageView? {
        get {
            if(layers.isEmpty || lastActiveLayerIndex == -1) {
                return nil
            }
            if(drawMode == "eraser"){
                return layers[lastActiveLayerIndex].mask as? UIImageView
            }
            return layers[lastActiveLayerIndex] as? UIImageView
        }
    }
    override public func touchesBegan(_ touches: Set<UITouch>,
                                      with event: UIEvent?){
        if isDrawing {
            addDrawLayer()
            swiped = false
            if let touch = touches.first {
                lastPoint = touch.location(in: self.drawImageView)
                firstPoint = touch.location(in: self.drawImageView) // for rectangle
            }
        }
    }
    
    override public func touchesMoved(_ touches: Set<UITouch>,
                                      with event: UIEvent?){
        let touchCount = event?.allTouches?.count;
        if isDrawing && touchCount == 1 && self.drawImageView != nil {
            // 6
            swiped = true
            if let touch = touches.first {
                let currentPoint = touch.location(in: self.drawImageView)
                if drawMode == "square" { // TODO: square -> rectangle
                  drawRectangle(lastPoint, toPoint: currentPoint, firstPoint: firstPoint)
                } else {
                  drawLineFrom(lastPoint, toPoint: currentPoint)
                }
                
                // 7
                lastPoint = currentPoint
            }
        }
    }
    
    override public func touchesEnded(_ touches: Set<UITouch>,
                                      with event: UIEvent?){
        let touchCount = event?.allTouches?.count;
        
        if (drawMode == "text" && touchCount == 1 && activeTextView == nil) {
            isTyping = true
            let touchPoint = touches.first!.location(in: self.view)
            
            let textView = UITextView(frame: CGRect(x: touchPoint.x, y: touchPoint.y,
                                                    width: self.firstActiveLayer.frame.width, height: toolSize+10))

            textView.font = UIFont(name: "Helvetica", size: toolSize)
            textView.textAlignment = .center
            textView.text = "Text"
            textView.textColor = toolColor
            textView.layer.shadowColor = UIColor.black.cgColor
            textView.layer.shadowOffset = CGSize(width: 1.0, height: 0.0)
            textView.layer.shadowOpacity = 0.2
            textView.layer.shadowRadius = 1.0
            textView.layer.backgroundColor = UIColor.clear.cgColor
            textView.autocorrectionType = .no
            textView.isScrollEnabled = false
            textView.delegate = self
            activeTextView = textView
            addLayer(layer: textView)
            addGestures(view: textView)
            textView.becomeFirstResponder()
        } else if(activeTextView != nil){
            activeTextView!.endEditing(true)
        }

        if (isDrawing && touchCount == 1 && self.drawImageView != nil) {
            if !swiped {
                // draw a single point
                if drawMode == "square" { // TODO: square -> rectangle
                  drawRectangle(lastPoint, toPoint: lastPoint, firstPoint: firstPoint)
                } else {
                  drawLineFrom(lastPoint, toPoint: lastPoint)
                }
            }
        }
    }
    
    func addDrawLayer(){
        let newImageView = UIImageView()
        newImageView.frame = self.firstActiveLayer.frame
        newImageView.bounds = self.firstActiveLayer.bounds
        
        if (drawMode == "marker") {
        newImageView.alpha = 0.5
        }
        if(drawMode == "eraser"){
            newImageView.image = self.firstActiveLayer.image
            let maskView = UIImageView(frame: CGRect(origin: CGPoint.zero, size: newImageView.frame.size))
            newImageView.mask = maskView;
        }
        addLayer(layer: newImageView)
    }
    
    func drawLineFrom(_ fromPoint: CGPoint, toPoint: CGPoint) {
        let canvasSize = self.drawImageView!.frame.integral.size
        UIGraphicsBeginImageContextWithOptions(canvasSize, false, 0)
        
        let color = drawMode == "eraser" ? UIColor.black : toolColor
        
        if let context = UIGraphicsGetCurrentContext() {
            self.drawImageView!.image?.draw(in: CGRect(x: 0, y: 0, width: canvasSize.width, height: canvasSize.height))
            // 2
            context.move(to: CGPoint(x: fromPoint.x, y: fromPoint.y))
            context.addLine(to: CGPoint(x: toPoint.x, y: toPoint.y))
            // 3
            context.setLineCap( CGLineCap.round)
            context.setLineWidth(toolSize)
            context.setStrokeColor(color.cgColor)
            context.setBlendMode( CGBlendMode.normal)
            // 4
            context.strokePath()
            // 5
            self.drawImageView!.image = UIGraphicsGetImageFromCurrentImageContext()
        }
        UIGraphicsEndImageContext()
    }

    func drawRectangle(_ fromPoint: CGPoint, toPoint: CGPoint, firstPoint: CGPoint) {
        let canvasSize = self.drawImageView!.frame.integral.size
        UIGraphicsBeginImageContextWithOptions(canvasSize, false, 0)
        
        let color = toolColor
        
        if let context = UIGraphicsGetCurrentContext() {
            self.drawImageView!.image?.draw(in: CGRect(x: 0, y: 0, width: canvasSize.width, height: canvasSize.height))
            
            context.clear(CGRect(x: 0, y: 0, width: canvasSize.width, height: canvasSize.height))
            
            context.setLineCap(CGLineCap.round)

            context.setLineWidth(toolSize)
            // 2
            context.move(to: CGPoint(x: fromPoint.x, y: fromPoint.y))
            context.addLine(to: CGPoint(x: toPoint.x, y: toPoint.y))
            
            // add lines
            context.move(to: CGPoint(x: firstPoint.x, y: firstPoint.y))
            context.addLine(to: CGPoint(x: toPoint.x, y: firstPoint.y))
            
            context.move(to: CGPoint(x: toPoint.x, y: firstPoint.y))
            context.addLine(to: CGPoint(x: toPoint.x, y: toPoint.y))
            
            context.move(to: CGPoint(x: toPoint.x, y: toPoint.y))
            context.addLine(to: CGPoint(x: firstPoint.x, y: toPoint.y))
            
            context.move(to: CGPoint(x: firstPoint.x, y: toPoint.y))
            context.addLine(to: CGPoint(x: firstPoint.x, y: firstPoint.y))
            
            // 3
            context.setStrokeColor(color.cgColor)
            context.setBlendMode(CGBlendMode.normal)
            // 4
            context.strokePath()
            // 5
            self.drawImageView!.image = UIGraphicsGetImageFromCurrentImageContext()
        }

        UIGraphicsEndImageContext()
    }
    
}
