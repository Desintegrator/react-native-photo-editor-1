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
  
interface NativePhotoEditorViewProps extends ViewProps {
  brushColor: string;
  rotationDegrees: number;
  source: Source | number;
  mode: PhotoEditorModeType;
}

export interface PhotoEditorViewProps extends NativePhotoEditorViewProps {
  gesturesEnabled?: boolean
  minScale?: number
  maxScale?: number
}
