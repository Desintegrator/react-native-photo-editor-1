import React, { useEffect, useRef, forwardRef, useImperativeHandle } from "react";
import { findNodeHandle, Platform, UIManager } from "react-native";
import { PhotoEditorViewManager } from './PhotoEditorViewManager';
import { PhotoEditorViewProps, IPhotoEditorViewRef } from "./types";

const createFragment = (viewId:number|null) =>
    UIManager.dispatchViewManagerCommand(
        viewId,
        //@ts-ignore
        UIManager.RNPhotoEditorViewManager?.Commands?.create?.toString(),
        [viewId]
    );

const clearAll = (viewId:number|null) =>
    UIManager.dispatchViewManagerCommand(
        viewId,
        //@ts-ignore
        UIManager.RNPhotoEditorViewManager?.Commands?.clearAll?.toString(),
        [viewId]
    );

const PhotoEditorView = forwardRef<IPhotoEditorViewRef, PhotoEditorViewProps>((props, ref) => {
  const editorRef = useRef(null);

  useImperativeHandle(ref, () => ({
    clearAll() {
      const viewId = findNodeHandle(editorRef.current);
      clearAll(viewId);
    }
  }));


  useEffect(() => {
    if(Platform.OS === 'android'){
      const timeoutId = setTimeout(()=>{
        const viewId = findNodeHandle(editorRef.current);
        createFragment(viewId);
      }, 300)
      return ()=>clearTimeout(timeoutId);
    }
  }, []);

  return (
      <PhotoEditorViewManager
          {...props}
          ref={editorRef}
      />
  );
});

export default PhotoEditorView;
