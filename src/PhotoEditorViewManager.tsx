import {  HostComponent, requireNativeComponent } from 'react-native';
import { NativePhotoEditorViewProps } from './types';

export const PhotoEditorViewManager: HostComponent<NativePhotoEditorViewProps> =
requireNativeComponent("RNPhotoEditorView")

