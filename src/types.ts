import {  UIManagerStatic, ViewProps } from "react-native";

type Source = {
  uri?: string;
  headers?: {
      [key: string]: string;
  };
};

export type RNPhotoEditorCommands = 'create'|'clearAll'|'crop'|'rotate'|'redo'|'undo'|'reload'|'processPhoto'

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

export interface onPhotoProcessedEvent {
  nativeEvent: {
      path: string;
  };
}

export interface onLayersUpdateEvent {
  nativeEvent: {
    layersCount: number,
    activeLayer: number
  };
}

export interface NativePhotoEditorViewProps extends ViewProps {
  toolColor: string;
  toolSize: number;
  source: Source | number;
  mode: PhotoEditorModeType;
  onImageLoadError?(e:onImageLoadErrorEvent): void;
  onLayersUpdate?(e:onLayersUpdateEvent): void;
  onPhotoProcessed?(e:onPhotoProcessedEvent): void;
}

export interface IPhotoEditorViewRef {
  clearAll(): void;
  rotate(clockwise?:boolean): void;
  crop(): void;
  redo(): void;
  undo(): void;
  reload(): void;
  processPhoto(): void;
}

export interface PhotoEditorViewProps extends NativePhotoEditorViewProps {
  onMount?(): void
  gesturesEnabled?: boolean
  minScale?: number
  maxScale?: number
}
