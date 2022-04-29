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
    
    @objc
    func setSource(source:NSDictionary){
        let url:URL = URL(string: source["uri"] as! String)!;
        let headers: NSDictionary = source["headers"] as! NSDictionary;
      
        var request = URLRequest(url: url);
        for (key,value) in headers {
            request.addValue((key as! String), forHTTPHeaderField:  value as! String);
        }
        
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            if let data = data {
                let image = UIImage(data: data)
                DispatchQueue.main.async {
                    self.photoEditor.setImageView(image: image!)
                }
            } else if let error = error {
                print("HTTP Request Failed \(error)")
            }
        }
        task.resume()

    }
}
