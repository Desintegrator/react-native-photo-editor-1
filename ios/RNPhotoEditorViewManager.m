#import "RNPhotoEditorViewManager.h"
#import "RNPhotoEditorView.h"

@implementation RNPhotoEditorViewManager

RCT_EXPORT_MODULE(RNPhotoEditorView)

-(UIView *)view
{
  return [[RNPhotoEditorView alloc] init];
}

@end
