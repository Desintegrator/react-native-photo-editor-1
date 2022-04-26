import { ViewProps } from "react-native";

export interface PhotoEditorViewProps extends ViewProps {
  brushColor: string;
  uri: string|null;
}