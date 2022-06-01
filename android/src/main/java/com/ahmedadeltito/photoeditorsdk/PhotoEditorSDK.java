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

  private final Context context;
  private final RelativeLayout parentView;
  private final ImageView imageView;
  private final BrushDrawingView brushDrawingView;
  private final List<View> addedViews;
  private OnPhotoEditorSDKListener onPhotoEditorSDKListener;
  private EditText activeEditTextView;
  private int lastActiveLayerIndex = 0;
  private final List<Integer> cropImagesIndexesList;

  private boolean textEditingEnabled = false;

  private PhotoEditorSDK(PhotoEditorSDKBuilder photoEditorSDKBuilder) {
    this.context = photoEditorSDKBuilder.context;
    this.parentView = photoEditorSDKBuilder.parentView;
    this.imageView = photoEditorSDKBuilder.imageView;
    this.brushDrawingView = photoEditorSDKBuilder.brushDrawingView;
    addedViews = new ArrayList<>();
    cropImagesIndexesList = new ArrayList<>();
  }

  public ImageView getMainView() {
    int firstActiveLayerIndex = getFirstActiveLayerIndex();
    if (firstActiveLayerIndex == -1) return imageView;
    return addedViews.get(firstActiveLayerIndex).findViewById(R.id.fragment_photo_cropped_image);
  }

  public void addCroppedImage(Bitmap croppedImage) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View imageRootView = inflater.inflate(R.layout.fragment_photo_cropped_image_view, null);
    ImageView imageView = (ImageView) imageRootView.findViewById(R.id.fragment_photo_cropped_image);
    imageView.setImageBitmap(croppedImage);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    parentView.addView(imageRootView, params);
    addCropImageIndex(lastActiveLayerIndex);
    addLayer(imageRootView);
    updateViewsLayout();
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
    addLayer(imageRootView);
    updateViewsLayout();
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
    addLayer(addTextRootView);
    activeEditTextView = addTextView;
    addTextRootView.requestFocus();
    updateViewsLayout();
  }

  private void addLayer(View view) {
    // delete layers hidden by undo
    while (addedViews.size() > lastActiveLayerIndex) {
      parentView.removeView(addedViews.remove(lastActiveLayerIndex));
    }
    addedViews.add(lastActiveLayerIndex, view);
    lastActiveLayerIndex++;
    updateViewsLayout();
  }


  private int getFirstActiveLayerIndex() {
    if (cropImagesIndexesList.size() > 0) {
      return cropImagesIndexesList.get(cropImagesIndexesList.size() - 1);
    }
    return -1;
  }

  public void undo() {
    if (lastActiveLayerIndex > 0 && addedViews.size() >= lastActiveLayerIndex) {
      lastActiveLayerIndex--;
      if (getFirstActiveLayerIndex() >= lastActiveLayerIndex) {
        popCropImageIndex();
      }
      addedViews.get(lastActiveLayerIndex).setVisibility(View.INVISIBLE);
      updateViewsLayout();
    }
  }

  public void redo() {
    if (addedViews.size() >= lastActiveLayerIndex + 1) {
      View addedView = addedViews.get(lastActiveLayerIndex);
      if (addedView.getId() == R.id.fragment_photo_cropped_image_view) {
        addCropImageIndex(lastActiveLayerIndex);
      }
      addedView.setVisibility(View.VISIBLE);
      lastActiveLayerIndex++;
      updateViewsLayout();
    }
  }

  private void addCropImageIndex(int index) {
    cropImagesIndexesList.add(index);
    updateLayersVisible();
  }

  private void popCropImageIndex() {
    int lastIndex = cropImagesIndexesList.size() - 1;
    cropImagesIndexesList.remove(lastIndex);
    updateLayersVisible();
  }

  private void updateLayersVisible() {
    int firstActiveLayerIndex = getFirstActiveLayerIndex();
    imageView.setVisibility(firstActiveLayerIndex == -1 ? View.VISIBLE : View.INVISIBLE);
    for (int i = 0; i < addedViews.size(); i++) {
      View layer = addedViews.get(i);
      if (i >= firstActiveLayerIndex && i <= lastActiveLayerIndex) {
        layer.setVisibility(View.VISIBLE);
      } else layer.setVisibility(View.INVISIBLE);
    }
  }

  public void enableTextEditing() {
    textEditingEnabled = true;
  }

  public void updateViewsLayout() {
    ImageView view = getMainView();
    RelativeLayout.LayoutParams params = view.getWidth() > 0
        ? new RelativeLayout.LayoutParams(view.getWidth(), view.getHeight())
        : new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    params.addRule(RelativeLayout.ALIGN_LEFT, view.getId());
    params.addRule(RelativeLayout.ALIGN_TOP, view.getId());
    params.addRule(RelativeLayout.ALIGN_RIGHT, view.getId());
    params.addRule(RelativeLayout.ALIGN_BOTTOM, view.getId());
    brushDrawingView.setLayoutParams(params);
  }

  public void disableTextEditing() {
    if (activeEditTextView != null) {
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

  public void brushEraser() {
    if (brushDrawingView != null)
      brushDrawingView.brushEraser();
  }


  public void hideViews() {
    this.imageView.setVisibility(View.INVISIBLE);
    for (View layer : this.addedViews) {
      layer.setVisibility(View.INVISIBLE);
    }
  }

  public void showViews() {
    int firstActiveLayerIndex = getFirstActiveLayerIndex();
    if (firstActiveLayerIndex == -1) {
      this.imageView.setVisibility(View.VISIBLE);
    }
    for (int i = 0; i < lastActiveLayerIndex; i++) {
      if (i < firstActiveLayerIndex) continue;
      View layer = this.addedViews.get(i);
      layer.setVisibility(View.VISIBLE);
    }
  }

  public void clearAllViews() {
    cropImagesIndexesList.clear();
    for (int i = 0; i < addedViews.size(); i++) {
      parentView.removeView(addedViews.get(i));
    }
    addedViews.clear();
    showViews();
  }

  public Bitmap generateImage() {
    int firstActiveLayerIndex = getFirstActiveLayerIndex();
    ImageView imageView = firstActiveLayerIndex == -1
        ? this.imageView
        : (ImageView) addedViews.get(firstActiveLayerIndex).findViewById(R.id.fragment_photo_cropped_image);
//        : parentView.findViewById(R.id.fragment_photo_cropped_image);
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
    if (textEditingEnabled) {
      activeEditTextView = view;
      view.requestFocus();
    }
  }

  @Override
  public void onRemoveViewListener(View removedView) {
  }

  public static class PhotoEditorSDKBuilder {

    private final Context context;
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
