/* eslint-disable react-native/no-inline-styles */
import React, {useState, useEffect} from 'react';
import {StyleSheet, Text, TouchableOpacity, View} from 'react-native';
import RNFS from 'react-native-fs';
import PhotoEditorView from 'react-native-photo-editor';

const PHOTO_PATH = RNFS.DocumentDirectoryPath + '/photo.jpg';

export default function App() {
  const [brushColor, setBrushColor] = useState('black');
  const [editedImagePath, setEditedImagePath] = useState<string | null>(null);

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

  useEffect(() => {
    RNFS.downloadFile({
      fromUrl:
        'https://i.pinimg.com/originals/e4/9b/c4/e49bc442a5cd920fc72e5105fa7ee52e.png',
      toFile: PHOTO_PATH,
      background: true,
    })
      .promise.then(response => {
        if (response.statusCode === 200) {
          setEditedImagePath(PHOTO_PATH);
        }
      })
      .catch(error => {
        console.log({error});
      });
  }, []);

  console.log(editedImagePath);

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
          uri={editedImagePath}
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
