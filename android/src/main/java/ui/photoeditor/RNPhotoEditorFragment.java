package ui.photoeditor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ahmedadeltito.photoeditorsdk.BrushDrawingView;
import com.ahmedadeltito.photoeditorsdk.OnPhotoEditorSDKListener;
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK;
import com.ahmedadeltito.photoeditorsdk.ViewType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.canhub.cropper.CropImageView;
import com.canhub.cropper.CropOverlayView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RNPhotoEditorFragment extends Fragment implements OnPhotoEditorSDKListener {
  private RelativeLayout parentImageRelativeLayout;
  private ImageView photoEditImageView;
  private CropImageView cropImageView;
  private CropOverlayView cropOverlayView;
  private BrushDrawingView brushDrawingView;
  private Bitmap editedImage;
  public PhotoEditorSDK photoEditorSDK;
  private int brushColor = Color.BLACK;
  private String mode = "none";
  private EditedImageSource editedImageSource;

  public interface OnImageLoadErrorListener {
    void onError(String error);
  }

  private OnImageLoadErrorListener onImageLoadErrorListener;

  public void setOnImageLoadErrorListener(OnImageLoadErrorListener onImageLoadErrorListener) {
    this.onImageLoadErrorListener = onImageLoadErrorListener;
  }

  public void setBrushColor(int brushColor) {
    this.brushColor = brushColor;
    if (photoEditorSDK != null) {
      photoEditorSDK.setBrushColor(brushColor);
    }
  }

  public void setMode(String mode) {
    this.mode = mode;
    updateEditorMode();
  }

  public void setEditedImageSource(EditedImageSource editedImageSource) {
    this.editedImageSource = editedImageSource;
    updateEditorImage();
  }

  public void clearAllViews() {
    if (photoEditorSDK != null) {
      photoEditorSDK.clearAllViews();
    }
  }

  public void rotate(boolean clockwise){
    if (mode.equals("crop") && photoEditorSDK != null && cropImageView != null){
      Log.d("DEV", "rotate: "+clockwise);
      cropImageView.rotateImage(clockwise?90:-90);
    }
  }

  public void updateEditorMode() {
    if (photoEditorSDK != null) {
      List<String> drawableActions = Arrays.asList("pencil", "marker");
      photoEditorSDK.setBrushDrawingMode(drawableActions.contains(mode));
      if (mode.equals("crop")) {
        enableCrop();
      } else {
        dismissCrop();
      }
    }
  }

  public void enableCrop() {
    if (editedImage != null) {
      cropImageView.setImageBitmap(editedImage);
      cropImageView.resetCropRect();
    }
  }

  public void submitCrop() {
    if (cropImageView != null && editedImage != null) {
      try {
        Bitmap croppedImage = cropImageView.getCroppedImage();
        if (croppedImage != null) {
          photoEditImageView.setImageBitmap(croppedImage);
          editedImage = croppedImage;
          updateBrushDrawingView();
          dismissCrop();
        }
      } catch (Exception e) {
        Log.d("DEV", "submitCrop: " + e);
      }
    }
  }

  public void dismissCrop() {
    cropImageView.clearImage();
  }

  private void loadGlideImage(CustomTarget<Bitmap> target) {
    Glide
        .with(getContext())
        .asBitmap()
        .load(editedImageSource.getSourceForLoad()).into(target);
  }

  public void updateEditorImage() {
    if (photoEditImageView != null && editedImageSource != null) {
      loadGlideImage(new CustomTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
          if (photoEditImageView != null) {
            photoEditImageView.setImageBitmap(resource);
            updateBrushDrawingView();
            editedImage = resource;
          }
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
          if (photoEditImageView != null) {
            photoEditImageView.setImageDrawable(placeholder);
          }
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
          super.onLoadFailed(errorDrawable);
          if (onImageLoadErrorListener != null) {
            onImageLoadErrorListener.onError("Failed to load image");
          }

        }
      });
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    super.onCreateView(inflater, parent, savedInstanceState);
    int layout = R.layout.fragment_photo_editor;
    return inflater.inflate(layout, parent, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    parentImageRelativeLayout = (RelativeLayout) view.findViewById(R.id.parent_image_rl);
    brushDrawingView = (BrushDrawingView) view.findViewById(R.id.drawing_view);
    photoEditImageView = (ImageView) view.findViewById(R.id.photo_edit_iv);
    cropImageView = (CropImageView) view.findViewById(R.id.crop_view);
    cropOverlayView = (CropOverlayView) view.findViewById(R.id.crop_overlay);
    RelativeLayout deleteRelativeLayout = (RelativeLayout) view.findViewById(R.id.delete_rl);
    photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(this.getContext())
        .parentView(parentImageRelativeLayout) // add parent image view
        .childView(photoEditImageView) // add the desired image view
        .deleteView(deleteRelativeLayout) // add the deleted view that will appear during the movement of the views
        .brushDrawingView(brushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
        .buildPhotoEditorSDK(); // build photo editor sdk
    photoEditorSDK.setOnPhotoEditorSDKListener(this);
    photoEditorSDK.setBrushColor(brushColor);
    updateEditorMode();
    updateEditorImage();
    setupCropImageView();
    setupCropOverlayView();
  }

  private void updateBrushDrawingView() {
    if (brushDrawingView != null && photoEditImageView != null) {
//      int inWidth = photoEditImageView.getDrawable().getIntrinsicWidth();
//      int inHeight = photoEditImageView.getDrawable().getIntrinsicHeight();
//      ViewGroup.LayoutParams layoutParams = brushDrawingView.getLayoutParams();
//      layoutParams.height=inHeight;
//      layoutParams.width=inWidth;
//      brushDrawingView.setLayoutParams(layoutParams);
//      brushDrawingView.setLayoutParams(photoEditImageView.getLayoutParams());
    }
  }

  private void setupCropOverlayView() {
    if (cropOverlayView != null) {
      cropOverlayView.setCropShape(CropImageView.CropShape.RECTANGLE);
    }
  }

  private void setupCropImageView() {
    if (cropImageView != null) {
      cropImageView.setAutoZoomEnabled(true);
      cropImageView.setFixedAspectRatio(false);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onEditTextChangeListener(String text, int colorCode) {

  }

  @Override
  public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

  }

  @Override
  public void onRemoveViewListener(int numberOfAddedViews) {

  }

  @Override
  public void onStartViewChangeListener(ViewType viewType) {

  }

  @Override
  public void onStopViewChangeListener(ViewType viewType) {
    if(viewType == ViewType.BRUSH_DRAWING){
      Bitmap bitmap = brushDrawingView.getImageBitmap();
      if(bitmap != null){
        photoEditorSDK.addImage(bitmap);
        brushDrawingView.clearAll();
      }
    }
  }
}
