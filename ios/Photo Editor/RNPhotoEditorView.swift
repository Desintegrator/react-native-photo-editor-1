import Foundation
import UIKit

@objc
class RNPhotoEditorView: UIView {
    var photoEditor: PhotoEditorViewController!;
    override init(frame: CGRect) {
        super.init(frame: frame)
        photoEditor = PhotoEditorViewController(nibName:"PhotoEditorViewController",bundle: Bundle(for: PhotoEditorViewController.self));
        let photoEditorView = photoEditor.view;
        photoEditorView?.frame = self.bounds;
//        photoEditorView?.isMultipleTouchEnabled = true;
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
    
    @objc var brushColor = UIColor.black {
        didSet {
            photoEditor.drawColor = self.brushColor;
            self.setupView()
        }
    }
    
    // TODO@korotkov: add image load errors handlers
    @objc
    func setSource(source:NSDictionary){
        let url:URL = URL(string: source["uri"] as! String)!;
        let headers: NSDictionary = source["headers"] as! NSDictionary;
        var headersFileds = [String: String]()
        
        for (key, value) in headers {
            headersFileds[key as! String] = (value as! String)
        }
        var request = URLRequest(url: url);
        request.allHTTPHeaderFields = headersFileds;
        let sessionConfiguration = URLSessionConfiguration.default;
        for (key,value) in headers {
            request.addValue((value as! String), forHTTPHeaderField:  key as! String);
            if(key as! String == "Authorization"){
                sessionConfiguration.httpAdditionalHeaders = [
                    "Authorization": value
                ]
            }
        }

        request.httpMethod = "GET"
        let session = URLSession(configuration: sessionConfiguration)
        let task = session.dataTask(with: request) { data, response, error in
            if let data = data {
                let image = UIImage(data: data)
                DispatchQueue.main.async {
                    if(image != nil){
                        self.photoEditor.setImageView(image: image!)
                    }
                }
            } else if let error = error {
                print("HTTP Request Failed \(error)")
            }
        }
        task.resume()

    }
}
