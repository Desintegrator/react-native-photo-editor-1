import { ViewProps } from "react-native";

type Source = {
  uri?: string;
  headers?: {
      [key: string]: string;
  };
};

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

interface NativePhotoEditorViewProps extends ViewProps {
  brushColor: string;
  toolSize: number;
  rotationDegrees: number;
  source: Source | number;
  mode: PhotoEditorModeType;
  onImageLoadError?(e:onImageLoadErrorEvent): void;
}

export interface IPhotoEditorViewRef {
  clearAll(): void;
}

export interface PhotoEditorViewProps extends NativePhotoEditorViewProps {
  gesturesEnabled?: boolean
  minScale?: number
  maxScale?: number
}
