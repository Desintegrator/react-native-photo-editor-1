import Foundation

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
    
}
