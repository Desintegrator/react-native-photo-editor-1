import React,{ useEffect, useRef } from "react";
import { findNodeHandle, PixelRatio, UIManager, ViewProps } from "react-native";
import { PhotoEditorViewManager } from './PhotoEditorViewManager';


const createFragment = (viewId:number|null) =>
UIManager.dispatchViewManagerCommand(
  viewId,
  //@ts-ignore
  UIManager.RNPhotoEditorViewManager?.Commands?.create?.toString(),
  [viewId]
);

interface PhotoEditorViewProps extends ViewProps {
  brushColor: string;
  uri: string|null;
}

const PhotoEditorView:React.FC<PhotoEditorViewProps> = (props) => {
const ref = useRef(null);

useEffect(() => {
  const timeoutId = setTimeout(()=>{
    const viewId = findNodeHandle(ref.current);
    createFragment(viewId);    
  }, 300)
  return ()=>clearTimeout(timeoutId);
}, []); 

return (
  <PhotoEditorViewManager
    {...props}
    ref={ref}
  />
);
};

export default PhotoEditorView;