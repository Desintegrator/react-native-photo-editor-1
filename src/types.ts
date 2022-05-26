import {  UIManagerStatic, ViewProps } from "react-native";

type Source = {
  uri?: string;
  headers?: {
      [key: string]: string;
  };
};

export type RNPhotoEditorCommands = 'create'|'clearAll'|'crop'|'rotate'

export interface RNPhotoEditorUIManager extends UIManagerStatic {
  RNPhotoEditorView: {
    Commands: { [key in RNPhotoEditorCommands]: number };
  };
}

export type PhotoEditorModeType =
  | 'pencil'
  | 'marker'
  | 'text'
  | 'crop'
  | 'eraser'
  | 'square'
  | 'none';


export interface onImageLoadErrorEvent {
  nativeEvent: {
      error: any;
  };
}

export interface NativePhotoEditorViewProps extends ViewProps {
  brushColor: string;
  toolSize: number;
  rotationDegrees: number;
  source: Source | number;
  mode: PhotoEditorModeType;
  onImageLoadError?(e:onImageLoadErrorEvent): void;
}

export interface IPhotoEditorViewRef {
  clearAll(): void;
  rotate(clockwise?:boolean): void;
  crop(): void;
}

export interface PhotoEditorViewProps extends NativePhotoEditorViewProps {
  gesturesEnabled?: boolean
  minScale?: number
  maxScale?: number
}
