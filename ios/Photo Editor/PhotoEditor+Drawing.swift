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
        let subViews = self.view.subviews
        if (isDrawing && drawMode != "undo" && drawMode != "redo") {
            // when drawing - remove all unvisible layers
            for subview in subViews {
                if (subview.alpha == 0) {
                    self.layers.remove(at: self.layers.count - 1)
                    subview.removeFromSuperview()
                }
            }
        }
        if (drawMode == "undo") {
          // make latest layer unvisible
          if (self.layers.count > 0 && self.activeLayerNumber > -1) {
            for subview in subViews {
                if subview.tag == 90005 + self.activeLayerNumber + 2 && subview.alpha == 1 {
                    subview.alpha = 0
                    self.activeLayerNumber = self.layers.count - 1
                    break
                }
            }
          }
          return
        }
        if (drawMode == "redo") {
          // make first unvisible layer visible
            if (self.layers.count > 0) {
                for subview in subViews {
                    if subview.tag == 90005 + self.activeLayerNumber + 2 && subview.alpha == 0 {
                        subview.alpha = 1
                        self.activeLayerNumber = self.activeLayerNumber + 1
                        break
                    }
                }
            }
          return
        }

        if isDrawing {
            if (self.layers.count < 10) {
                let newImageView = UIImageView()
                newImageView.frame = self.canvasImageView.frame
                newImageView.bounds = self.canvasImageView.bounds
                newImageView.tag = 90005 + self.layers.count + 1
                if (drawMode == "marker") {
                  newImageView.alpha = 0.5
                }
                self.layers.append(newImageView)
                self.view.addSubview(newImageView)
                self.activeLayerNumber = self.layers.count - 1
            } else {
              // merge two oldest layers
            }

            swiped = false
            if let touch = touches.first {
                lastPoint = touch.location(in: self.layers[self.activeLayerNumber])
                firstPoint = touch.location(in: self.layers[self.activeLayerNumber]) // for rectangle
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
                let currentPoint = touch.location(in: self.layers[self.activeLayerNumber])
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

        if (drawMode == "text" && touchCount == 1) {
          isTyping = true
          let textView = UITextView(frame: CGRect(x: 0, y: self.layers[self.activeLayerNumber].center.y,
                                                  width: UIScreen.main.bounds.width, height: toolSize))
          
          textView.textAlignment = .center
          textView.font = UIFont(name: "Helvetica", size: toolSize)
          textView.textColor = toolColor
          textView.layer.shadowColor = UIColor.black.cgColor
          textView.layer.shadowOffset = CGSize(width: 1.0, height: 0.0)
          textView.layer.shadowOpacity = 0.2
          textView.layer.shadowRadius = 1.0
          textView.layer.backgroundColor = UIColor.clear.cgColor
          textView.autocorrectionType = .no
          textView.isScrollEnabled = false
          textView.delegate = self
          self.layers[self.activeLayerNumber].addSubview(textView)
          addGestures(view: textView)
          textView.becomeFirstResponder()

          return
        }

        if (isDrawing && touchCount == 1) {
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
        let canvasSize = self.layers[self.activeLayerNumber].frame.integral.size
        UIGraphicsBeginImageContextWithOptions(canvasSize, false, 0)
        
        let color = toolColor
        
        if let context = UIGraphicsGetCurrentContext() {
            self.layers[self.activeLayerNumber].image?.draw(in: CGRect(x: 0, y: 0, width: canvasSize.width, height: canvasSize.height))
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
            self.layers[self.activeLayerNumber].image = UIGraphicsGetImageFromCurrentImageContext()
        }
        UIGraphicsEndImageContext()
    }

    func drawRectangle(_ fromPoint: CGPoint, toPoint: CGPoint, firstPoint: CGPoint) {
        let canvasSize = self.layers[self.activeLayerNumber].frame.integral.size
        UIGraphicsBeginImageContextWithOptions(canvasSize, false, 0)
        
        let color = toolColor
        
        if let context = UIGraphicsGetCurrentContext() {
            self.layers[self.activeLayerNumber].image?.draw(in: CGRect(x: 0, y: 0, width: canvasSize.width, height: canvasSize.height))
            
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
            self.layers[self.activeLayerNumber].image = UIGraphicsGetImageFromCurrentImageContext()
        }

        UIGraphicsEndImageContext()
    }
    
}
