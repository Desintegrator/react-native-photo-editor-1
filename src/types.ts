import { ViewProps } from "react-native";

type Source = {
  uri?: string;
  headers?: {
      [key: string]: string;
  };
};

interface NativePhotoEditorViewProps extends ViewProps {
  brushColor: string;
  rotationDegrees: number;
  source: Source | number;
  mode: 
  | 'pencil'
  | 'marker'
  | 'text'
  | 'crop'
  | 'eraser'
  | 'square' 
  | "none";
}

export interface PhotoEditorViewProps extends NativePhotoEditorViewProps {
  gesturesEnabled?: boolean
  minScale?: number
  maxScale?: number
}
