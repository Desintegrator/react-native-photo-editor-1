package ui.photoeditor;

import android.graphics.Color;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;


public class RNPhotoEditorViewManager extends SimpleViewManager<FrameLayout> {
  public static final String REACT_CLASS = "RNPhotoEditorView";
  public final int COMMAND_CREATE = 1;

  ReactApplicationContext reactContext;
  RNPhotoEditorFragment photoEditorFragment;

  private int brushColor;
  private String mode;
  private EditedImageSource editedImage;

  public RNPhotoEditorViewManager(ReactApplicationContext reactContext) {
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  public FrameLayout createViewInstance(ThemedReactContext themedReactContext) {
    return new RNPhotoEditorView(reactContext);

  }

  @Nullable
  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of("create", COMMAND_CREATE);
  }

  @Override
  public void receiveCommand(
      @NonNull FrameLayout root,
      String commandId,
      @Nullable ReadableArray args
  ) {
    super.receiveCommand(root, commandId, args);
    int reactNativeViewId = args.getInt(0);
    int commandIdInt = Integer.parseInt(commandId);

    switch (commandIdInt) {
      case COMMAND_CREATE:
        createFragment(root, reactNativeViewId);
        break;
      default: {}
    }
  }


  @ReactProp(name = "source")
  public void setSrc(FrameLayout view, @Nullable ReadableMap source) {
    if (source == null || !source.hasKey("uri") || isNullOrEmpty(source.getString("uri"))) {
      return;
    }
    final EditedImageSource imageSource = ImageSourceConverter.getImageSource(view.getContext(), source);
    if (imageSource.getUri().toString().length() == 0) {
      return;
    }
    editedImage = imageSource;
    updatePhotoEditorImage();
  }

  @ReactProp(name = "brushColor")
  public void setBrushColor(FrameLayout view, @Nullable String color) {
    if (color != null) {
      this.brushColor = Color.parseColor(color);
      updatePhotoEditorBrushColor();
    }
  }

  @ReactProp(name = "mode")
  public void setMode(FrameLayout view, @Nullable String mode) {
    if(mode != null){
      this.mode = mode;
      updatePhotoEditorMode();
    }
  }

  private void updatePhotoEditorBrushColor(){
    if(photoEditorFragment != null){
      photoEditorFragment.setBrushColor(brushColor);
    }
  }


  private void updatePhotoEditorMode(){
    if(photoEditorFragment != null){
      photoEditorFragment.setMode(mode);
    }
  }

  private void updatePhotoEditorImage(){
    if(photoEditorFragment != null){
      photoEditorFragment.setEditedImageSource(editedImage);
    }
  }

  public void createFragment(FrameLayout root, int reactNativeViewId) {
    ViewGroup parentView = (ViewGroup) root.findViewById(reactNativeViewId);
    photoEditorFragment = new RNPhotoEditorFragment();
    FragmentActivity activity = (FragmentActivity) reactContext.getCurrentActivity();
    if(activity != null){
      activity.getSupportFragmentManager()
          .beginTransaction()
          .replace(reactNativeViewId, photoEditorFragment, String.valueOf(reactNativeViewId))
          .commit();
      setupLayout(parentView);
      updatePhotoEditorImage();
      updatePhotoEditorBrushColor();
      updatePhotoEditorMode();
    }
  }

  public void setupLayout(ViewGroup view) {
    Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
      @Override
      public void doFrame(long frameTimeNanos) {
        manuallyLayoutChildren(view);
        view.getViewTreeObserver().dispatchOnGlobalLayout();
        Choreographer.getInstance().postFrameCallback(this);
      }
    });
  }

  public void manuallyLayoutChildren(ViewGroup view) {
    for(int i=0;i<view.getChildCount();i++){
      View child = view.getChildAt(i);
      child.measure(
        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY));
      child.layout(0,0,child.getMeasuredWidth(),child.getMeasuredHeight());
    }
  }

  private boolean isNullOrEmpty(final String url) {
    return url == null || url.trim().isEmpty();
  }

}
