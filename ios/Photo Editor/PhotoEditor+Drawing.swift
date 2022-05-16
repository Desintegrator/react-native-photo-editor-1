//
//  PhotoEditor+Drawing.swift
//  Photo Editor
//
//  Created by Mohamed Hamed on 6/16/17.
//
//

import UIKit

extension PhotoEditorViewController {
    
    override public func touchesBegan(_ touches: Set<UITouch>,
                                      with event: UIEvent?){
        if isDrawing {
            swiped = false
            if let touch = touches.first {
                lastPoint = touch.location(in: self.canvasImageView)
                firstPoint = touch.location(in: self.canvasImageView) // for rectangle
            }
        }       
    }
    
    override public func touchesMoved(_ touches: Set<UITouch>,
                                      with event: UIEvent?){
        let touchCount = event?.allTouches?.count;

        if isDrawing && touchCount == 1 {
            // 6
            swiped = true
            if let touch = touches.first {
                let currentPoint = touch.location(in: canvasImageView)
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

        if drawMode == "text" && touchCount == 1 {
          isTyping = true
          let textView = UITextView(frame: CGRect(x: 0, y: canvasImageView.center.y,
                                                  width: UIScreen.main.bounds.width, height: toolSize))
          
          textView.textAlignment = .center
          textView.font = UIFont(name: "Helvetica", size: toolSize)
          textView.textColor = drawColor
          textView.layer.shadowColor = UIColor.black.cgColor
          textView.layer.shadowOffset = CGSize(width: 1.0, height: 0.0)
          textView.layer.shadowOpacity = 0.2
          textView.layer.shadowRadius = 1.0
          textView.layer.backgroundColor = UIColor.clear.cgColor
          textView.autocorrectionType = .no
          textView.isScrollEnabled = false
          textView.delegate = self
          self.canvasImageView.addSubview(textView)
          addGestures(view: textView)
          textView.becomeFirstResponder()

          return
        }

        if isDrawing && touchCount == 1 {
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
    
    func drawLineFrom(_ fromPoint: CGPoint, toPoint: CGPoint) {
        let canvasSize = canvasImageView.frame.integral.size
        UIGraphicsBeginImageContextWithOptions(canvasSize, false, 0)
        
        let color = drawColor.withAlphaComponent(drawMode == "marker" ? 0.5 : 1.0)
        
        if let context = UIGraphicsGetCurrentContext() {
            canvasImageView.image?.draw(in: CGRect(x: 0, y: 0, width: canvasSize.width, height: canvasSize.height))
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
            canvasImageView.image = UIGraphicsGetImageFromCurrentImageContext()
        }
        UIGraphicsEndImageContext()
    }

    func drawRectangle(_ fromPoint: CGPoint, toPoint: CGPoint, firstPoint: CGPoint) {
        let canvasSize = canvasImageView.frame.integral.size
        UIGraphicsBeginImageContextWithOptions(canvasSize, false, 0)
        
        let color = drawColor
        
        if let context = UIGraphicsGetCurrentContext() {
            canvasImageView.image?.draw(in: CGRect(x: 0, y: 0, width: canvasSize.width, height: canvasSize.height))
            
            // TODO: clears ALL!!! should clear only its layer
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
            canvasImageView.image = UIGraphicsGetImageFromCurrentImageContext()
        }

        UIGraphicsEndImageContext()
    }
    
}
