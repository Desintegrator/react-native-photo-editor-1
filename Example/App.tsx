/* eslint-disable react-native/no-inline-styles */
import React, {useCallback, useRef, useState} from 'react';
import {
  StyleSheet,
  TouchableOpacity,
  View,
  Text,
  SafeAreaView,
} from 'react-native';
import {GestureHandlerRootView} from 'react-native-gesture-handler';
import PhotoEditorView, {
  IPhotoEditorViewRef,
  PhotoEditorViewProps,
  PhotoEditorModeType,
} from '@scm/react-native-photo-editor';

const PHOTO_PATH =
  // 'https://images.unsplash.com/photo-1526512340740-9217d0159da9?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2677&q=80';
  'https://i.pinimg.com/originals/e4/9b/c4/e49bc442a5cd920fc72e5105fa7ee52e.png';

const HEADERS = {};

const Button = ({
  title,
  onPress,
  isActive,
}: {
  title: string;
  onPress(): void;
  isActive?: boolean;
}) => (
  <TouchableOpacity
    onPress={onPress}
    style={[
      styles.action,
      {
        backgroundColor: isActive ? 'grey' : 'black',
      },
    ]}>
    <Text style={{color: 'white'}}>{title}</Text>
  </TouchableOpacity>
);

export default function App() {
  const [toolColor, setToolColor] = useState('black');
  const [mode, setMode] = useState<PhotoEditorViewProps['mode']>('none');
  const [photoEditorVisible, setPhotoEditorVisible] = useState(true);
  const ref = useRef<IPhotoEditorViewRef>(null);

  const ColorButton: React.FC<{color: string; onPress?: () => void}> =
    useCallback(({color, onPress}) => {
      const _onPress = onPress || (() => setToolColor(color));
      return (
        <TouchableOpacity
          onPress={_onPress}
          style={{
            flex: 1,
            backgroundColor: color,
          }}
        />
      );
    }, []);

  const ActionButton: React.FC<{action: PhotoEditorModeType}> = useCallback(
    ({action}) => {
      const onPress = () => {
        setMode(mode === action ? 'none' : action);
      };
      return (
        <Button title={action} isActive={mode === action} onPress={onPress} />
      );
    },
    [mode],
  );

  const rotate = () => {
    ref.current?.rotate();
  };

  const clearAll = () => {
    ref.current?.clearAll();
  };

  const submit = () => {
    if (mode === 'crop') {
      ref.current?.crop();
    }
    setMode('none');
  };

  const reset = () => {
    setMode('none');
    setPhotoEditorVisible(false);
    setTimeout(() => {
      setPhotoEditorVisible(true);
    });
  };

  return (
    <GestureHandlerRootView style={styles.container}>
      <SafeAreaView style={styles.container}>
        <View style={styles.header}>
          <Button title="Clear all" onPress={clearAll} />
          <Button title="Reset" onPress={reset} />
        </View>
        <View style={styles.editorContainer}>
          {photoEditorVisible && (
            <PhotoEditorView
              ref={ref}
              style={styles.editorView}
              toolSize={10}
              toolColor={toolColor}
              mode={mode}
              source={{
                uri: PHOTO_PATH,
                headers: HEADERS,
              }}
              onImageLoadError={(e: any) => {
                console.log('ERROR', e.nativeEvent.error);
              }}
            />
          )}
        </View>
        <View style={styles.subactions}>
          {['pencil', 'marker', 'square', 'text'].includes(mode) && (
            <>
              <ColorButton color={'black'} />
              <ColorButton color={'red'} />
              <ColorButton color={'green'} />
              <ColorButton color={'blue'} />
              <ColorButton color={'yellow'} />
            </>
          )}
          {mode === 'crop' && (
            <>
              <Button title="Rotate" onPress={rotate} />
            </>
          )}
          {['crop', 'text'].includes(mode) && (
            <>
              <Button title="Submit" onPress={submit} />
            </>
          )}
        </View>
        <View style={{flexDirection: 'row'}}>
          <ActionButton action="pencil" />
          <ActionButton action="marker" />
          <ActionButton action="crop" />
          <ActionButton action="text" />
          <ActionButton action="eraser" />
          <ActionButton action="square" />
        </View>
      </SafeAreaView>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  editorView: {
    flex: 1,
    width: '100%',
  },
  container: {
    flex: 1,
    backgroundColor: '#3F3F46',
  },
  header: {
    paddingTop: 40,
    backgroundColor: 'rgba(55, 65, 81, 0.8)',
    flexDirection: 'row',
  },
  subactions: {
    backgroundColor: 'rgba(55, 65, 81, 0.8)',
    height: 40,
    flexDirection: 'row',
  },
  editorContainer: {
    flex: 1,
  },
  button: {
    flex: 1,
  },
  action: {
    flex: 1,
    height: 50,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'black',
    borderWidth: 1,
    borderColor: 'white',
  },
});
