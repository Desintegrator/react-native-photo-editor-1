import React,{ useRef } from "react";
import { PhotoEditorViewManager } from './PhotoEditorViewManager';
import { PhotoEditorViewProps } from "./types";

const PhotoEditorView:React.FC<PhotoEditorViewProps> = (props) => {
const ref = useRef(null);

return (
  <PhotoEditorViewManager
    {...props}
    ref={ref}
  />
);
};

export default PhotoEditorView;