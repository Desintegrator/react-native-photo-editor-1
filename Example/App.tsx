/* eslint-disable react-native/no-inline-styles */
import React, {useState} from 'react';
import {StyleSheet, TouchableOpacity, View, Text} from 'react-native';
import {GestureHandlerRootView} from 'react-native-gesture-handler';
import PhotoEditorView from 'react-native-photo-editor';

const PHOTO_PATH =
  // 'https://images.unsplash.com/photo-1526512340740-9217d0159da9?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2677&q=80';
  'https://i.pinimg.com/originals/e4/9b/c4/e49bc442a5cd920fc72e5105fa7ee52e.png';

const HEADERS = {};

export default function App() {
  const [brushColor, setBrushColor] = useState('black');
  const [rotationDegrees, setRotationDegrees] = useState(0);

  const Button: React.FC<{color: string; onPress?: () => void}> = ({
    color,
    onPress,
  }) => {
    const _onPress = onPress || (() => setBrushColor(color));
    return (
      <TouchableOpacity
        onPress={_onPress}
        style={{
          flex: 1,
          backgroundColor: color,
        }}
      />
    );
  };

  const rotateEditorView = (clockwise = true) => {
    let _rotationDegrees = rotationDegrees + (clockwise ? 90 : -90);
    if (_rotationDegrees >= 360) {
      _rotationDegrees = 0;
    }
    if (_rotationDegrees < 0) {
      _rotationDegrees = 270;
    }
    setRotationDegrees(_rotationDegrees);
  };

  const editorViewStyles = {
    transform: [{rotateZ: '' + rotationDegrees + 'deg'}],
  };

  return (
    <GestureHandlerRootView style={styles.container}>
      <View style={styles.container}>
        <View style={styles.header} />
        <View style={styles.editorContainer}>
          <PhotoEditorView
            style={[styles.editorView, editorViewStyles]}
            brushColor={brushColor}
            rotationDegrees={rotationDegrees}
            source={{
              uri: PHOTO_PATH,
              headers: HEADERS,
            }}
          />
        </View>
        <View
          style={{
            height: 100,
            width: 100,
          }}>
          <Text style={{color: 'white'}}>Rotate</Text>
          <Button color={'gray'} onPress={rotateEditorView} />
        </View>
        <View style={styles.footer}>
          <Button color={'black'} />
          <Button color={'red'} />
          <Button color={'green'} />
          <Button color={'blue'} />
          <Button color={'yellow'} />
        </View>
      </View>
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
    backgroundColor: 'rgba(55, 65, 81, 0.8)',
    height: 100,
  },
  footer: {
    backgroundColor: 'rgba(55, 65, 81, 0.8)',
    height: 60,
    flexDirection: 'row',
  },
  editorContainer: {
    flex: 1,
  },
  button: {
    flex: 1,
  },
});
