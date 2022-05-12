import React,{ useEffect, useRef, useState } from "react";
import {  findNodeHandle, LayoutChangeEvent, Platform, StyleSheet, UIManager, View, Dimensions } from "react-native";
import { Gesture, GestureDetector, gestureHandlerRootHOC } from "react-native-gesture-handler";
import Animated, { useAnimatedStyle, useSharedValue, withTiming } from "react-native-reanimated";
import { useDerivedValue } from "react-native-reanimated";
import { PhotoEditorViewManager } from './PhotoEditorViewManager';
import {  PhotoEditorViewProps } from "./types";

const DEFAULT_MIN_SCALE = 0.1
const DEFAULT_MAX_SCALE = 2.5

const {
  width: WINDOW_WIDTH, 
  height: WINDOW_HEIGHT
} = Dimensions.get('window');

const createFragment = (viewId:number|null) =>
UIManager.dispatchViewManagerCommand(
  viewId,
  //@ts-ignore
  UIManager.RNPhotoEditorView?.Commands?.create?.toString(),
  [viewId]
);


const PhotoEditorView:React.FC<PhotoEditorViewProps> = ({
  gesturesEnabled = true,
  minScale = DEFAULT_MIN_SCALE,
  maxScale = DEFAULT_MAX_SCALE,
  ...rest}) => {
  const ref = useRef(null);
  
  const [imageWidth, setImageWidth] = useState(WINDOW_WIDTH)
  const [imageHeight, setImageHeight] = useState(WINDOW_HEIGHT)

  useEffect(() => {
    if(Platform.OS === 'android'){    
      const timeoutId = setTimeout(()=>{
        const viewId = findNodeHandle(ref.current);
        createFragment(viewId);    
      }, 300)
      return ()=>clearTimeout(timeoutId);
    }
  }, []); 

  const offsetX = useSharedValue(0);
  const offsetY = useSharedValue(0);
  const start = useSharedValue({ x: 0, y: 0 });
  const scale = useSharedValue(1);
  const savedScale = useSharedValue(1);

  const maxOffsetX = useDerivedValue(()=>(scale.value * imageWidth - imageWidth) / 2);
  const maxOffsetY = useDerivedValue(()=>(scale.value * imageHeight - imageHeight) / 2);

  const dragGesture = Gesture.Pan()
    .enabled(gesturesEnabled && rest.mode === 'none')
    .averageTouches(true)
    .onUpdate((e) => {
      if(scale.value > 1){
        const newOffsetX = e.translationX+start.value.x;
        const newOffsetY = e.translationY+start.value.y;        
        if(newOffsetX>-maxOffsetX.value&&newOffsetX<maxOffsetX.value){
          offsetX.value = e.translationX+start.value.x;
        }        
        if(newOffsetY>-maxOffsetY.value&&newOffsetY<maxOffsetY.value){
          offsetY.value = e.translationY+start.value.y;
        }
      }
    })
    .onEnd(() => {      
      start.value = {
        x: offsetX.value,
        y: offsetY.value,
      };
    });

const zoomGesture = Gesture.Pinch()
  .enabled(gesturesEnabled)
  .cancelsTouchesInView(true)
  .onUpdate((event) => {
    const newScale = savedScale.value*event.scale;    
    if(newScale > minScale && newScale < maxScale){
      scale.value = savedScale.value * event.scale;
    }
    if(scale.value < 1) {
      offsetX.value = withTiming(0);
      offsetY.value = withTiming(0);
      start.value = {
        x: 0,
        y: 0,
      };
    }else{
      if(offsetX.value<-maxOffsetX.value){
        offsetX.value = -maxOffsetX.value
      } else if (offsetX.value>maxOffsetX.value){
        offsetX.value = maxOffsetX.value
      }
    }
  })
  .onEnd(() => {
    savedScale.value = scale.value;    
  });

  const composedGesture = Gesture.Simultaneous(
    dragGesture,
    zoomGesture
  );

  const animatedStyle = useAnimatedStyle(()=>({
    transform:[    
      { translateX: offsetX.value },
      { translateY: offsetY.value },
      { scale: scale.value },
    ]
  }));

  const onImageLayout = ({nativeEvent:{layout:{width,height}}}:LayoutChangeEvent)=>{  
    setImageWidth(width)
    setImageHeight(height)
  }

  return (
    <View style={styles.container}>
      <GestureDetector gesture={composedGesture}>
        <Animated.View style={[styles.container,animatedStyle]}>
          <PhotoEditorViewManager
            onLayout={onImageLayout}
            {...rest}
            ref={ref}
          />
        </Animated.View>
      </GestureDetector>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex:1
  }
})

export default gestureHandlerRootHOC(PhotoEditorView);