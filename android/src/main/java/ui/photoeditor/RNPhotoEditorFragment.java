package ui.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ahmedadeltito.photoeditor.UtilFunctions;
import com.ahmedadeltito.photoeditorsdk.BrushDrawingView;
import com.ahmedadeltito.photoeditorsdk.OnPhotoEditorSDKListener;
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK;
import com.ahmedadeltito.photoeditorsdk.ViewType;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.react.views.imagehelper.ImageSource;

import java.io.File;

public class RNPhotoEditorFragment extends Fragment implements OnPhotoEditorSDKListener {
  private RelativeLayout parentImageRelativeLayout;
  private ImageView photoEditImageView;

  public PhotoEditorSDK photoEditorSDK;
  private int brushColor = Color.BLACK;
  private EditedImageSource editedImageSource;

  public void setBrushColor(int brushColor) {
    this.brushColor = brushColor;
    if(photoEditorSDK != null){
      photoEditorSDK.setBrushColor(brushColor);
    }
  }

  public void setEditedImageSource(EditedImageSource editedImageSource) {
    this.editedImageSource = editedImageSource;
    updateEditorImage();
  }


  public void updateEditorImage(){
    if(photoEditImageView != null && editedImageSource != null){

      Glide.with(getContext()).asBitmap().load(editedImageSource.getSourceForLoad()).into(new CustomTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
          photoEditImageView.setImageBitmap(resource);
        }
        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
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
    BrushDrawingView brushDrawingView = (BrushDrawingView) view.findViewById(R.id.drawing_view);
    photoEditImageView = (ImageView) view.findViewById(R.id.photo_edit_iv);
    RelativeLayout deleteRelativeLayout = (RelativeLayout) view.findViewById(R.id.delete_rl);
    Bitmap bitmap = Bitmap.createBitmap(500,500, Bitmap.Config.ARGB_8888);
    bitmap.eraseColor(Color.WHITE);
    photoEditImageView.setImageBitmap(bitmap);
    photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(this.getContext())
        .parentView(parentImageRelativeLayout) // add parent image view
        .childView(photoEditImageView) // add the desired image view
        .deleteView(deleteRelativeLayout) // add the deleted view that will appear during the movement of the views
        .brushDrawingView(brushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
        .buildPhotoEditorSDK(); // build photo editor sdk
    photoEditorSDK.setOnPhotoEditorSDKListener(this);
    photoEditorSDK.setBrushColor(brushColor);
    photoEditorSDK.setBrushDrawingMode(true);
    updateEditorImage();
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

  }
}
