<h1 align="center">

<p align="center">
  <img src="https://lh3.googleusercontent.com/dsJXfHnUx0qvZIB_80F-q0iN18eIqmx6g10bmsVN8R6nEnLQDKvJ9lXCbnPCgDEZMw=s180"/>
</p>

ReactNative: Native Photo Editor (Android/iOS)

</h1>
This library is a React Native bridge around native photo editor libraries. It allows you to edit any photo by providing below set of features:


* _**Cropping**_
* _**Adding Images -Stickers-**_
* _**Adding Text with Colors**_
* _**Drawing with Colors**_
* _**Scaling and Rotating Objects**_
* _**Deleting Objects**_
* _**Saving to Photos and Sharing**_
* _**Cool Animations**_

<img src="assets/hero.gif" />

## 📖 Getting started

`$ yarn add react-native-photo-editor`

> This library is supported React Native V61 and above

- **iOS**

> **iOS Prerequisite:** Please make sure `CocoaPods` is installed on your system

	- Add the following to your `Podfile` -> `ios/Podfile` and run pod update:

```

  post_install do |installer|
    installer.pods_project.targets.each do |target|
      if target.name.include?('iOSPhotoEditor')
        target.build_configurations.each do |config|
          config.build_settings['SWIFT_VERSION'] = '5'
        end
      end
    end
  end

```

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


- Add below activity in your app activities:

`
<activity android:name="com.ahmedadeltito.photoeditor.PhotoEditorActivity" />
<activity android:name="com.yalantis.ucrop.UCropActivity" />
`

- To save image to the public external storage, you must request the WRITE_EXTERNAL_STORAGE permission in your manifest file:

`<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`

## ⛄️ Stickers

If you want stickers, please add them to your native project:

* **iOS:** Add stickers to iOS Resources folder
* **Android:** Add stickers to app `drawable` folder

> Refer Example project for the same.

## 💻 Usage

```
import PhotoEditor from 'react-native-photo-editor'

```

> * Purpose of this library is to edit photos which are within app sandbox, we recommend to move captured image to app sandbox then using RNFS share image path with library for the edit.

> * Example: If we capture image through cameraRoll then we should first move image to app sandbox using RNFS then share app storage path with the editor.

## 💡 Props

- **General(iOS & Android)**

| Prop                   | Type                | Default | Note                                             |
| ---------------------- | ------------------- | ------- | ------------------------------------------------ |
| `path: mandatory`     | `string`            |         | Specify image path you want to edit                 |
| `hiddenControls`                | `array`            |         | Specify editor controls you want to hide `[clear, crop, draw, save, share, sticker, text]`                        |
| `stickers`          | `array`            |         | Specify stickers you want to show in stickers picker                  |
| `colors`     | `array: HEX-COLOR` |    `[#000000, #808080, #a9a9a9, #FFFFFF, #0000ff, #00ff00, #ff0000, #ffff00, #ffa500, #800080, #00ffff, #a52a2a, #ff00ff]`     | Specify colors you want to show for draw/text              |
| `onDone`    | `func` |         | Specify done callback            |
| `onCancel`        | `func`            |      | Specify cancel callback       |

## 🔧 Troubleshooting
### If using React Native Firebase v6+ or facing any of the following issues: 
  - Add the following to your `podfile -> ios/podfile` and run `pod install`
```
pre_install do |installer|
  installer.pod_targets.each do |pod|
    if pod.name.start_with?('RNFB')
      def pod.build_type;
        Pod::BuildType.static_library
      end
    end
  end
end
```

  - If the above doesn't work, try the following and and re-run `pod install`:

As [@react-native-firebase documentation](https://rnfirebase.io/#allow-ios-static-frameworks) you should add following to top of the Podfile for Allow iOS Static Frameworks
```
$RNFirebaseAsStaticFramework = true
```

### [__swift_FORCE_LOAD_$_swiftUniformTypeIdentifiers / __swift_FORCE_LOAD_$_swiftCoreMIDI](https://github.com/prscX/react-native-photo-editor/issues/171)

## ✨ Credits

- Android Photo Editor: [eventtus/photo-editor-android](https://github.com/eventtus/photo-editor-android)
- iOS Photo Editor: [eventtus/photo-editor](https://github.com/eventtus/photo-editor)

## 📜 License
This library is provided under the Apache 2 License.

RNPhotoEditor @ [prscX](https://github.com/prscX)
