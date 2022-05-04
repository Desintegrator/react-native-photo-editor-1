import {  HostComponent, requireNativeComponent } from 'react-native';

export const PhotoEditorViewManager =
  requireNativeComponent("RNPhotoEditorView") as HostComponent<any>;

