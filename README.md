<h1 align="center">
ReactNative: Native Photo Editor (Android/iOS)
</h1>

This library is a React Native UI Component for image editing. It allows you to edit any photo by providing below set of features:

* _**Cropping**_
* _**Adding Text with Colors**_
* _**Drawing with Colors**_
* _**Undo/Redo**_
* _**Saving to Photos and Sharing**_

## 📖 Getting started

`$ yarn add @scm/react-native-photo-editor`

- **Android**

- Please add below script in your build.gradle

```
buildscript {
    repositories {
        maven { url "https://jitpack.io" }
        ...
    }
}

allprojects {
    repositories {
        maven { url "https://jitpack.io" }
        ...
    }
}
```

- To save image to the public external storage, you must request the WRITE_EXTERNAL_STORAGE permission in your manifest file:

`<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`

## 💻 Usage

See Example folder
```js
import PhotoEditorView from '@scm/react-native-photo-editor'

```

## ✨ Credits
- React Native Photo Editor [prscX/react-native-photo-editor](https://github.com/prscX/react-native-photo-editor)
- Android Photo Editor: [eventtus/photo-editor-android](https://github.com/eventtus/photo-editor-android)
- iOS Photo Editor: [eventtus/photo-editor](https://github.com/eventtus/photo-editor)
- Android Image Cropper: [CanHub/Android-Image-Cropper](https://github.com/CanHub/Android-Image-Cropper)

## 📜 License
This library is provided under the Apache 2 License.
