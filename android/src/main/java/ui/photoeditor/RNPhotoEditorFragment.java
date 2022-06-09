package ui.photoeditor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ahmedadeltito.photoeditorsdk.BrushDrawingView;
import com.ahmedadeltito.photoeditorsdk.EraserDrawingView;
import com.ahmedadeltito.photoeditorsdk.OnPhotoEditorSDKListener;
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK;
import com.ahmedadeltito.photoeditorsdk.ViewType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.canhub.cropper.CropImageView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class RNPhotoEditorFragment extends Fragment implements OnPhotoEditorSDKListener {
  private RelativeLayout parentImageRelativeLayout;
  private ImageView photoEditImageView;
  private CropImageView cropImageView;
  private BrushDrawingView brushDrawingView;
  private EraserDrawingView eraserDrawingView;
  private Bitmap editedImage;
  public PhotoEditorSDK photoEditorSDK;
  private int toolColor = Color.BLACK;
  private int toolSize = 10;
  private String mode = "none";
  private EditedImageSource editedImageSource;

  // image error
  public interface OnImageLoadErrorListener {
    void onError(String error);
  }

  private OnImageLoadErrorListener onImageLoadErrorListener;

  public void setOnImageLoadErrorListener(OnImageLoadErrorListener onImageLoadErrorListener) {
    this.onImageLoadErrorListener = onImageLoadErrorListener;
  }

  // photo processed
  public interface OnPhotoProcessedListener {
    void onError(String error);
  }

  private OnPhotoProcessedListener onPhotoProcessedListener;

  public void setOnPhotoProcessedListener(OnPhotoProcessedListener onPhotoProcessedListener) {
    this.onPhotoProcessedListener = onPhotoProcessedListener;
  }
  // 

  public void setToolColor(int toolColor) {
    this.toolColor = toolColor;
    if (photoEditorSDK != null) {
      if (isDrawableMode()) {
        photoEditorSDK.setBrushColor(toolColor);
      }
      if (isTextMode()) {
        photoEditorSDK.setTextColor(toolColor);
      }
    }
  }

  public void setToolSize(int toolSize) {
    this.toolSize = toolSize;
    if (photoEditorSDK != null) {
      if (isDrawableMode()) {
        photoEditorSDK.setBrushSize(toolSize);
      }
      if (isTextMode()) {
        photoEditorSDK.setTextSize(toolSize);
      }
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
      reloadEraser();
    }
  }

  public void undo() {
    if (photoEditorSDK != null) {
      photoEditorSDK.undo();
      reloadCrop();
      reloadEraser();
    }
  }

  public void redo() {
    if (photoEditorSDK != null) {
      photoEditorSDK.redo();
      reloadCrop();
      reloadEraser();
    }
  }

  private void reloadEraser() {
    if (mode.equals("eraser")) {
      photoEditorSDK.setEraserDrawingMode(true);
    }
  }

  public void reload() {
    Log.d("TEST", "reload=>");
    // TODO: call "onLayersUpdate" here
  }

  public void processPhoto() {
    Log.d("TEST", "processPhoto=>");
  }

  public void rotate(boolean clockwise) {
    if (mode.equals("crop") && photoEditorSDK != null && cropImageView != null) {
      cropImageView.rotateImage(clockwise ? 90 : -90);
    }
  }

  private boolean isDrawableMode() {
    List<String> drawableActions = Arrays.asList("pencil", "marker");
    return drawableActions.contains(mode);
  }

  private boolean isTextMode() {
    return mode.equals("text");
  }

  public void updateEditorMode() {
    if (photoEditorSDK != null) {
      photoEditorSDK.setBrushDrawingMode(isDrawableMode());
      if (isDrawableMode()) {
        photoEditorSDK.setBrushColor(toolColor);
        photoEditorSDK.setBrushAlpha(mode.equals("marker") ? 120 : 255);
      }
      if (mode.equals("crop")) {
        enableCrop();
      } else {
        dismissCrop();
      }
      if (isTextMode()) {
        photoEditorSDK.enableTextEditing();
      } else {
        photoEditorSDK.disableTextEditing();
      }
      if (mode.equals("eraser")) {
        photoEditorSDK.setEraserDrawingMode(true);
        photoEditorSDK.updateViewsLayout();
      }

    }
  }

  private final View.OnTouchListener onParentViewTouchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      if (isTextMode()) {
        int action = event.getAction();
        float touchX = event.getX();
        float touchY = event.getY();
        if ((action & event.getActionMasked()) == MotionEvent.ACTION_UP) {
          photoEditorSDK.addTextField(touchX, touchY, toolColor, toolSize);
        }
      }
      return true;
    }
  };

  public void reloadCrop() {
    if (mode.equals("crop")) {
      dismissCrop();
      enableCrop();
    }
  }

  public void enableCrop() {
    if (photoEditorSDK != null) {
      cropImageView.setImageBitmap(photoEditorSDK.generateImage());
      photoEditorSDK.hideViews();
      cropImageView.resetCropRect();
    }
  }

  public void submitCrop() {
    if (cropImageView != null && editedImage != null) {
      try {
        int parentWidth = parentImageRelativeLayout.getWidth();
        int parentHeight = parentImageRelativeLayout.getHeight();
        Bitmap croppedImage = cropImageView.getCroppedImage(parentWidth, parentHeight, CropImageView.RequestSizeOptions.RESIZE_FIT);
        if (croppedImage != null) {
          photoEditorSDK.addCroppedImage(croppedImage);
          dismissCrop();


        }
      } catch (Exception ignored) {
      }
    }
  }

  public void dismissCrop() {
    cropImageView.clearImage();
    photoEditorSDK.showViews();
  }

  private void loadGlideImage(CustomTarget<Bitmap> target) {
    Glide
        .with(Objects.requireNonNull(getContext()))
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

        // public void onPhotoProcessed() {
        //   if (onPhotoProcessedListener != null) {
        //     onPhotoProcessedListener
        //   }
        // }

        // public void onLayersUpdate() {
        //   if (onLayersUpdateistener != null) {
        //     onLayersUpdateListener
        //   }
        // }
      });
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    super.onCreateView(inflater, parent, savedInstanceState);
    int layout = R.layout.fragment_photo_editor;
    return inflater.inflate(layout, parent, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    parentImageRelativeLayout = (RelativeLayout) view.findViewById(R.id.parent_image_rl);
    brushDrawingView = (BrushDrawingView) view.findViewById(R.id.drawing_view);
    photoEditImageView = (ImageView) view.findViewById(R.id.photo_edit_iv);
    cropImageView = (CropImageView) view.findViewById(R.id.crop_view);
    eraserDrawingView = (EraserDrawingView) view.findViewById(R.id.eraser_view);
    photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(this.getContext())
        .parentView(parentImageRelativeLayout) // add parent image view
        .childView(photoEditImageView) // add the desired image view
        .brushDrawingView(brushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
        .eraserDrawingView(eraserDrawingView) // add the brush drawing view that is responsible for drawing on the image view
        .buildPhotoEditorSDK(); // build photo editor sdk
    photoEditorSDK.setOnPhotoEditorSDKListener(this);
    photoEditorSDK.setBrushColor(toolColor);
    updateEditorMode();
    updateEditorImage();
    setupCropImageView();
    photoEditorSDK.updateViewsLayout();
    parentImageRelativeLayout.setOnTouchListener(this.onParentViewTouchListener);
  }

  private void setupCropImageView() {
    if (cropImageView != null) {
      cropImageView.setAutoZoomEnabled(false);
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
    if (viewType == ViewType.BRUSH_DRAWING) {
      Bitmap bitmap = brushDrawingView.getImageBitmap();
      if (bitmap != null) {
        photoEditorSDK.addImage(bitmap);
        brushDrawingView.clearAll();
      }
    }
    if (viewType == ViewType.ERASER_DRAWING) {
      Bitmap bitmap = eraserDrawingView.getImageBitmap();
      if (bitmap != null) {
        photoEditorSDK.addImage(bitmap);
        eraserDrawingView.clearAll();
      }
    }
  }
}
