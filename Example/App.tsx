/* eslint-disable react-native/no-inline-styles */
import React, {useState} from 'react';
import {StyleSheet, TouchableOpacity, View} from 'react-native';
import PhotoEditorView from 'react-native-photo-editor';

const PHOTO_PATH =
  'https://i.pinimg.com/originals/e4/9b/c4/e49bc442a5cd920fc72e5105fa7ee52e.png';

const HEADERS = {};

export default function App() {
  const [brushColor, setBrushColor] = useState('black');

  const Button: React.FC<{color: string}> = ({color}) => {
    const onPress = () => setBrushColor(color);
    return (
      <TouchableOpacity
        onPress={onPress}
        style={{
          flex: 1,
          backgroundColor: color,
        }}
      />
    );
  };

  return (
    <View style={styles.container}>
      <View style={styles.header} />
      <View style={styles.editorContainer}>
        <PhotoEditorView
          style={{
            flex: 1,
            width: '100%',
          }}
          brushColor={brushColor}
          source={{
            uri: PHOTO_PATH,
            headers: HEADERS,
          }}
        />
      </View>
      <View style={styles.footer}>
        <Button color={'black'} />
        <Button color={'red'} />
        <Button color={'green'} />
        <Button color={'blue'} />
        <Button color={'yellow'} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
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
