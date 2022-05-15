import Foundation
import UIKit
import SDWebImage

@objc
class RNPhotoEditorView: UIView {
    var photoEditor: PhotoEditorViewController!;
    override init(frame: CGRect) {
        super.init(frame: frame)
        photoEditor = PhotoEditorViewController(nibName:"PhotoEditorViewController",bundle: Bundle(for: PhotoEditorViewController.self));
        let photoEditorView = photoEditor.view;
        photoEditorView?.frame = self.bounds;
        photoEditorView?.autoresizingMask = [.flexibleHeight,.flexibleWidth]
        let emptyImage = UIColor.white.image(CGSize(width: 512, height: 256))
        photoEditor.setImageView(image: emptyImage);
        addSubview(photoEditorView!);
        setupView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }
    
    private func setupView() {
        photoEditor.loadViewIfNeeded()
    }
    
    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }
   
    @objc var mode: NSString = "none" {
        didSet {
            switch mode {
              case "pencil":
                  photoEditor.isDrawing = true;
                  photoEditor.drawMode = "pencil";
              case "marker":
                  photoEditor.isDrawing = true;
                  photoEditor.drawMode = "marker";
              case "square":
                  photoEditor.isDrawing = true;
                  photoEditor.drawMode = "square";
              case "text":
                  photoEditor.isDrawing = false;
                  photoEditor.drawMode = "text";
              default:
                  photoEditor.isDrawing = false;
                  photoEditor.drawMode = "";
            }
        }
    }

    @objc var toolSize: CGFloat = 50.0 {
        didSet {
            photoEditor.toolSize = self.toolSize;
            self.setupView()
        }
    }
    
    @objc var brushColor = UIColor.black {
        didSet {
            photoEditor.drawColor = self.brushColor;
            self.setupView()
        }
    }
    
    @objc var onImageLoadError: RCTDirectEventBlock?

    @objc
    func setSource(source:NSDictionary){
        let url:URL = URL(string: source["uri"] as! String)!;
        let headers: NSDictionary = source["headers"] as! NSDictionary;
        
        let requestModifier = SDWebImageDownloaderRequestModifier { (request) -> URLRequest? in
            if(request.url != nil){
                var mutableRequest = request;
                for (key, value) in headers {
                    mutableRequest.addValue(value as! String, forHTTPHeaderField: key as! String);
                }
                return mutableRequest;
            }
            return request
        };
        SDWebImageDownloader.shared.requestModifier = requestModifier;
        SDWebImageDownloader.shared.downloadImage(with: url) { image, data, error, finished in
            if(image != nil && finished){
                self.photoEditor.setImageView(image: image!);
            } else {
                if(self.onImageLoadError != nil){
                    let errorMsg = "Failed to load image: \(String(describing: error))";
                  self.onImageLoadError!(["error": errorMsg]);
                  }
            }
        }
    }
}
