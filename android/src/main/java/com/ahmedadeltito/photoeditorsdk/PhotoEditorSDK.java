package com.ahmedadeltito.photoeditorsdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import androidx.annotation.ColorInt;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import ui.photoeditor.R;

/**
 * Created by Ahmed Adel on 02/06/2017.
 */

public class PhotoEditorSDK implements MultiTouchListener.OnMultiTouchListener {

  private Context context;
  private RelativeLayout parentView;
  private ImageView imageView;
  private BrushDrawingView brushDrawingView;
  private List<View> addedViews;
  private OnPhotoEditorSDKListener onPhotoEditorSDKListener;
  private EditText activeEditTextView;
  private int firstLayerIndex = -1;
  private boolean textEditingEnabled = false;

  private PhotoEditorSDK(PhotoEditorSDKBuilder photoEditorSDKBuilder) {
    this.context = photoEditorSDKBuilder.context;
    this.parentView = photoEditorSDKBuilder.parentView;
    this.imageView = photoEditorSDKBuilder.imageView;
    this.brushDrawingView = photoEditorSDKBuilder.brushDrawingView;
    addedViews = new ArrayList<>();
  }

  public ImageView getMainView() {
    if (firstLayerIndex == -1) return imageView;
    return addedViews.get(firstLayerIndex).findViewById(R.id.fragment_photo_cropped_image);
  }

  public void addCroppedImage(Bitmap croppedImage) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View imageRootView = inflater.inflate(R.layout.fragment_photo_cropped_image_view, null);
    ImageView imageView = (ImageView) imageRootView.findViewById(R.id.fragment_photo_cropped_image);
    imageView.setImageBitmap(croppedImage);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    parentView.addView(imageRootView, params);
    addedViews.add(imageRootView);
    firstLayerIndex = addedViews.size() - 1;
    if (onPhotoEditorSDKListener != null)
      onPhotoEditorSDKListener.onAddViewListener(ViewType.IMAGE, addedViews.size());
  }

  public void addImage(Bitmap desiredImage) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View imageRootView = inflater.inflate(R.layout.photo_editor_sdk_image_item_list, null);
    ImageView imageView = (ImageView) imageRootView.findViewById(R.id.photo_editor_sdk_image_iv);
    imageView.setImageBitmap(desiredImage);
    imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT));
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    parentView.addView(imageRootView, params);
    addedViews.add(imageRootView);
    if (onPhotoEditorSDKListener != null)
      onPhotoEditorSDKListener.onAddViewListener(ViewType.IMAGE, addedViews.size());
  }

  public void addTextField(float x, float y, int colorCodeTextView, int textSize) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View addTextRootView = inflater.inflate(R.layout.photo_editor_sdk_text_field, null);
    EditText addTextView = (EditText) addTextRootView.findViewById(R.id.photo_editor_sdk_text_field);
    addTextView.setText(" ");
    if (colorCodeTextView != -1)
      addTextView.setTextColor(colorCodeTextView);
    if (textSize > 0) {
      addTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
    MultiTouchListener multiTouchListener = new MultiTouchListener(
        parentView, this.imageView, onPhotoEditorSDKListener);
    multiTouchListener.setOnMultiTouchListener(this);
    addTextRootView.setOnTouchListener(multiTouchListener);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.topMargin = (int) y - 12;
    params.leftMargin = (int) x - 12;
    parentView.addView(addTextRootView, params);
    addedViews.add(addTextRootView);
    activeEditTextView = addTextView;
    addTextRootView.requestFocus();
    if (onPhotoEditorSDKListener != null)
      onPhotoEditorSDKListener.onAddViewListener(ViewType.TEXT, addedViews.size());
  }


  public void enableTextEditing(){
    textEditingEnabled = true;
  }

  public void disableTextEditing() {
    if(activeEditTextView != null){
      activeEditTextView.clearFocus();
      activeEditTextView = null;
    }
    textEditingEnabled = false;
  }
  // DEPRECATED
  public void addText(String text, int colorCodeTextView) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View addTextRootView = inflater.inflate(R.layout.photo_editor_sdk_text_field, null);
    TextView addTextView = (TextView) addTextRootView.findViewById(R.id.photo_editor_sdk_text_field);
    addTextView.setGravity(Gravity.CENTER);
    addTextView.setText(text);
    if (colorCodeTextView != -1)
      addTextView.setTextColor(colorCodeTextView);
    MultiTouchListener multiTouchListener = new MultiTouchListener(
        parentView, this.imageView, onPhotoEditorSDKListener);
    multiTouchListener.setOnMultiTouchListener(this);
    addTextRootView.setOnTouchListener(multiTouchListener);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    parentView.addView(addTextRootView, params);
    addedViews.add(addTextRootView);
    if (onPhotoEditorSDKListener != null)
      onPhotoEditorSDKListener.onAddViewListener(ViewType.TEXT, addedViews.size());
  }


  public void setBrushDrawingMode(boolean brushDrawingMode) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushDrawingMode(brushDrawingMode);
  }

  public void setBrushSize(float size) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushSize(size);
  }

  public void setBrushColor(@ColorInt int color) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushColor(color);
  }

  public void setTextColor(@ColorInt int color) {
    if (activeEditTextView != null) {
      EditText editText = (EditText) activeEditTextView.findViewById(R.id.photo_editor_sdk_text_field);
      if (editText != null) {
        editText.setTextColor(color);
      }
    }
  }

  public void setTextSize(int size) {
    if (activeEditTextView != null) {
      activeEditTextView.setTextSize(size);
    }
  }

  public void setBrushAlpha(int alpha) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushAlpha(alpha);
  }

  public void setBrushEraserSize(float brushEraserSize) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushEraserSize(brushEraserSize);
  }

  public void setBrushEraserColor(@ColorInt int color) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushEraserColor(color);
  }

  public float getEraserSize() {
    if (brushDrawingView != null)
      return brushDrawingView.getEraserSize();
    return 0;
  }

  public float getBrushSize() {
    if (brushDrawingView != null)
      return brushDrawingView.getBrushSize();
    return 0;
  }

  public int getBrushColor() {
    if (brushDrawingView != null)
      return brushDrawingView.getBrushColor();
    return 0;
  }

  public void brushEraser() {
    if (brushDrawingView != null)
      brushDrawingView.brushEraser();
  }

  public void removeAddedViewByIndex(int index) {
    if (addedViews.size() > index) {
      parentView.removeView(addedViews.remove(index));
      if (onPhotoEditorSDKListener != null)
        onPhotoEditorSDKListener.onRemoveViewListener(addedViews.size());
    }
  }

  public void viewUndo() {
    if (addedViews.size() > 0) {
      parentView.removeView(addedViews.remove(addedViews.size() - 1));
      if (onPhotoEditorSDKListener != null)
        onPhotoEditorSDKListener.onRemoveViewListener(addedViews.size());
    }
  }

  private void viewUndo(View removedView) {
    if (addedViews.size() > 0) {
      if (addedViews.contains(removedView)) {
        parentView.removeView(removedView);
        addedViews.remove(removedView);
        if (onPhotoEditorSDKListener != null)
          onPhotoEditorSDKListener.onRemoveViewListener(addedViews.size());
      }
    }
  }

  public void hideViews() {
    this.imageView.setVisibility(View.INVISIBLE);
    for (View layer : this.addedViews) {
      layer.setVisibility(View.INVISIBLE);
    }
  }

  public void showViews() {
    if (firstLayerIndex == -1) {
      this.imageView.setVisibility(View.VISIBLE);
    }
    for (int i = 0; i < this.addedViews.size(); i++) {
      if (i < firstLayerIndex) continue;
      View layer = this.addedViews.get(i);
      layer.setVisibility(View.VISIBLE);
    }
  }

  public void clearAllViews() {
    firstLayerIndex = -1;
    for (int i = 0; i < addedViews.size(); i++) {
      parentView.removeView(addedViews.get(i));
    }
    addedViews.clear();
    if (onPhotoEditorSDKListener != null)
      onPhotoEditorSDKListener.onRemoveViewListener(0);
    showViews();
  }

  public Bitmap generateImage() {
    ImageView imageView = firstLayerIndex == -1
        ? this.imageView
        : parentView.findViewById(R.id.fragment_photo_cropped_image);

    parentView.setDrawingCacheEnabled(true);

    Bitmap image = Bitmap.createBitmap(
        parentView.getDrawingCache(true),
        imageView.getLeft(),
        imageView.getTop(),
        imageView.getWidth(),
        imageView.getHeight());
    parentView.setDrawingCacheEnabled(false);
    return image;
  }

  public String saveImage(String folderName, String imageName) {
    String selectedOutputPath = "";
    if (isSDCARDMounted()) {
      File mediaStorageDir = new File(
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName);
      // Create a storage directory if it does not exist
      if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
          Log.d("PhotoEditorSDK", "Failed to create directory");
        }
      }
      // Create a media file name
      selectedOutputPath = mediaStorageDir.getPath() + File.separator + imageName;
      File file = new File(selectedOutputPath);
      try {
        FileOutputStream out = new FileOutputStream(file);
        if (parentView != null) {
          parentView.setDrawingCacheEnabled(true);
          parentView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 80, out);
        }
        out.flush();
        out.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return selectedOutputPath;
  }

  private boolean isSDCARDMounted() {
    String status = Environment.getExternalStorageState();
    return status.equals(Environment.MEDIA_MOUNTED);
  }

  public void setOnPhotoEditorSDKListener(OnPhotoEditorSDKListener onPhotoEditorSDKListener) {
    this.onPhotoEditorSDKListener = onPhotoEditorSDKListener;
    brushDrawingView.setOnPhotoEditorSDKListener(onPhotoEditorSDKListener);
  }

  @Override
  public void onEditTextClickListener(EditText view) {
    if(textEditingEnabled){
      activeEditTextView = view;
      view.requestFocus();
    }
  }

  @Override
  public void onRemoveViewListener(View removedView) {
    viewUndo(removedView);
  }

  public static class PhotoEditorSDKBuilder {

    private Context context;
    private RelativeLayout parentView;
    private ImageView imageView;
    private BrushDrawingView brushDrawingView;

    public PhotoEditorSDKBuilder(Context context) {
      this.context = context;
    }

    public PhotoEditorSDKBuilder parentView(RelativeLayout parentView) {
      this.parentView = parentView;
      return this;
    }

    public PhotoEditorSDKBuilder childView(ImageView imageView) {
      this.imageView = imageView;
      return this;
    }


    public PhotoEditorSDKBuilder brushDrawingView(BrushDrawingView brushDrawingView) {
      this.brushDrawingView = brushDrawingView;
      return this;
    }

    public PhotoEditorSDK buildPhotoEditorSDK() {
      return new PhotoEditorSDK(this);
    }
  }
}
