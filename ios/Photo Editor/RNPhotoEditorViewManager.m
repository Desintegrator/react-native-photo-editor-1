#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(RNPhotoEditorViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(toolSize, CGFloat)
RCT_EXPORT_VIEW_PROPERTY(toolColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(mode, NSString)
RCT_EXPORT_VIEW_PROPERTY(needChangeConfirm, Bool)
RCT_EXPORT_VIEW_PROPERTY(onLayersUpdate, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onImageLoadError, RCTDirectEventBlock)

RCT_EXTERN_METHOD(clearAll:(nonnull NSNumber *)node)
RCT_EXTERN_METHOD(rotate: (nonnull NSNumber *)node clockwise:(BOOL)clockwise)
RCT_EXTERN_METHOD(crop:(nonnull NSNumber *)node)
RCT_EXTERN_METHOD(undo:(nonnull NSNumber *)node)
RCT_EXTERN_METHOD(redo:(nonnull NSNumber *)node)

RCT_CUSTOM_VIEW_PROPERTY(source, NSObject, RNPhotoEditorViewManager)
{
    [self performSelector:@selector(setSource:) withObject:@{@"view":view, @"json":json}];

}
@end
