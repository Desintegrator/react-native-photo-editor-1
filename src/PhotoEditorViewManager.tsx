import {  HostComponent, requireNativeComponent } from 'react-native';

export const PhotoEditorViewManager =
  requireNativeComponent('RNPhotoEditorViewManager') as HostComponent<any>;

