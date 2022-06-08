import Foundation
import React

@objc(RNPhotoEditorViewManager)
class RNPhotoEditorViewManager: RCTViewManager {
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
        return RNPhotoEditorView()
    }

    @objc(setSource:)
    public func setSource(obj:Dictionary<String, Any>) {
        guard let currentView = obj["view"] as? RNPhotoEditorView,
              let src = obj["json"] as? NSDictionary else {
                  return
              }

        currentView.setSource(source: src)
    }

    @objc func clearAll(_ node: NSNumber) {
        DispatchQueue.main.async {
            let component = self.bridge.uiManager.view(
                forReactTag: node
            ) as! RNPhotoEditorView
            component.clearAll()
        }
    }

    @objc func rotate(_ node: NSNumber, clockwise: Bool) {
        DispatchQueue.main.async {
            let component = self.bridge.uiManager.view(
                forReactTag: node
            ) as! RNPhotoEditorView
            component.rotate(clockwise:clockwise)
        }
    }
    
    @objc func crop(_ node: NSNumber) {
        DispatchQueue.main.async {
            let component = self.bridge.uiManager.view(
                forReactTag: node
            ) as! RNPhotoEditorView
            component.crop()
        }
    }

    @objc func reload(_ node: NSNumber) {
        DispatchQueue.main.async {
            let component = self.bridge.uiManager.view(
                forReactTag: node
            ) as! RNPhotoEditorView
            component.reload()
        }
    }

    @objc func processPhoto(_ node: NSNumber) {
        DispatchQueue.main.async {
            let component = self.bridge.uiManager.view(
                forReactTag: node
            ) as! RNPhotoEditorView
            component.processPhoto()
        }
    }

    @objc func undo(_ node: NSNumber) {
        DispatchQueue.main.async {
            let component = self.bridge.uiManager.view(
                forReactTag: node
            ) as! RNPhotoEditorView
            component.undo()
        }
    }
    @objc func redo(_ node: NSNumber) {
        DispatchQueue.main.async {
            let component = self.bridge.uiManager.view(
                forReactTag: node
            ) as! RNPhotoEditorView
            component.redo()
        }
    }
}
