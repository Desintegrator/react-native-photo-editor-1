import React,{ useEffect, useRef } from "react";
import { findNodeHandle, Platform, UIManager } from "react-native";
import { PhotoEditorViewManager } from './PhotoEditorViewManager';
import { PhotoEditorViewProps } from "./types";

const createFragment = (viewId:number|null) =>
UIManager.dispatchViewManagerCommand(
  viewId,
  //@ts-ignore
  UIManager.RNPhotoEditorView?.Commands?.create?.toString(),
  [viewId]
);

const PhotoEditorView:React.FC<PhotoEditorViewProps> = (props) => {
const ref = useRef(null);

useEffect(() => {
  if(Platform.OS === 'android'){    
    const timeoutId = setTimeout(()=>{
      const viewId = findNodeHandle(ref.current);
      createFragment(viewId);    
    }, 300)
    return ()=>clearTimeout(timeoutId);
  }
}, []); 

return (
  <PhotoEditorViewManager
    {...props}
    ref={ref}
  />
);
};

export default PhotoEditorView;