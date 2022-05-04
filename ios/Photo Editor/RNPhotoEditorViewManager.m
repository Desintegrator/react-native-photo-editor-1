#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(RNPhotoEditorViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(brushColor, UIColor)
RCT_CUSTOM_VIEW_PROPERTY(source, NSObject, RNPhotoEditorViewManager)
{
    [self performSelector:@selector(setSource:) withObject:@{@"view":view, @"json":json}];

}
@end
