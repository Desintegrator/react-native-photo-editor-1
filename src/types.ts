import { ViewProps } from "react-native";

type Source = {
  uri?: string;
  headers?: {
      [key: string]: string;
  };
};

export interface PhotoEditorViewProps extends ViewProps {
  brushColor: string;
  source: Source | number;
}

export interface IPhotoEditorViewRef {
  clearAll(): void;
}
