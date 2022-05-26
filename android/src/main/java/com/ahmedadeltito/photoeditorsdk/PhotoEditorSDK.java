package com.ahmedadeltito.photoeditorsdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import androidx.annotation.ColorInt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
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

public class PhotoEditorSDK implements MultiTouchListener.OnMultiTouchListener { //, OnTouchListener {

    private Context context;
    private RelativeLayout parentView;
    private ImageView imageView;
    private View deleteView;

    private BrushDrawingView brushDrawingView;
    private View brushDrawingRootView;

    private List<View> addedViews;
    private OnPhotoEditorSDKListener onPhotoEditorSDKListener;
    private View addTextRootView;

    private PhotoEditorSDK(PhotoEditorSDKBuilder photoEditorSDKBuilder) {
        this.context = photoEditorSDKBuilder.context;
        this.parentView = photoEditorSDKBuilder.parentView;
        this.imageView = photoEditorSDKBuilder.imageView;
        this.deleteView = photoEditorSDKBuilder.deleteView;
        this.brushDrawingView = photoEditorSDKBuilder.brushDrawingView;

        addedViews = new ArrayList<>();

        this.addDrawingLayer();
        this.addDrawingLayer();
        this.addDrawingLayer();
    }

    // @Override
    // public boolean OnMultiTouchListener(View view, MotionEvent event) {
    //   Log.d("TEST", "PhotoEditorSDK onTouchEvent");
    //   return true;
    // }

    // @Override
    // public boolean onTouch(View view, MotionEvent event) {
    //   super.onTouch(view, event);
    //   Log.d("TEST", "PhotoEditorSDK onTouchEvent");
    //   return true;
    // }

    // @Override
    // public boolean onTouchEvent(@NonNull MotionEvent event) {
    //     Log.d("TEST", "PhotoEditorSDK onTouchEvent");
    //     // if (brushDrawMode) {
    //     //     float touchX = event.getX();
    //     //     float touchY = event.getY();
    //     //     switch (event.getAction()) {
    //     //         case MotionEvent.ACTION_DOWN:
    //     //             drawPath.moveTo(touchX, touchY);
    //     //             if (onPhotoEditorSDKListener != null)
    //     //                 onPhotoEditorSDKListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
    //     //             break;
    //     //         case MotionEvent.ACTION_MOVE:
    //     //             drawPath.lineTo(touchX, touchY);
    //     //             break;
    //     //         case MotionEvent.ACTION_UP:
    //     //             drawCanvas.drawPath(drawPath, drawPaint);
    //     //             drawPath.reset();
    //     //             if (onPhotoEditorSDKListener != null)
    //     //                 onPhotoEditorSDKListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
    //     //             break;
    //     //         default:
    //     //             return false;
    //     //     }
    //     //     invalidate();
    //     //     return true;
    //     // } else {
    //     //     return false;
    //     // }
    // }

    // VIEWS ADDING -- start
    public void addDrawingLayer() {
      Log.d("TEST", "addDrawingLayer");

      // BrushDrawingView brushDrawingView = (BrushDrawingView) view.findViewById(R.id.drawing_view);
      // BrushDrawingView nextDrawingView = this.brushDrawingView;


      // LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      // BrushDrawingView nextDrawingView = (BrushDrawingView) this.brushDrawingView.findViewById(R.id.drawing_view);
      
      // BrushDrawingView nextDrawingView = (BrushDrawingView) inflater.inflate(R.id.drawing_view, null);

      // LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      // View drawingView = inflater.inflate(R.layout.photo_editor_sdk_image_item_list, null);

      // MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
      //           parentView, this.imageView, onPhotoEditorSDKListener);
      // multiTouchListener.setOnMultiTouchListener(this);

      // BrushDrawingView nextDrawingView = photoEditorSDKBuilder.brushDrawingView;

      // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
      //           ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      //   params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

      // ----------------------

      // BrushDrawingView nextDrawingView = (BrushDrawingView) this.brushDrawingView.findViewById(R.id.drawing_view);

      // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
      //           ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      // parentView.addView(nextDrawingView, params);
      // addedViews.add(nextDrawingView);

      // ----------------------

      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      brushDrawingRootView = inflater.inflate(R.layout.photo_editor_drawing_item_list, null);
      BrushDrawingView nextDrawingView = (BrushDrawingView) brushDrawingRootView.findViewById(R.id.drawing_view);

      // MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
      //           parentView, this.imageView, onPhotoEditorSDKListener);
      //   multiTouchListener.setOnMultiTouchListener(this);
      //   brushDrawingRootView.setOnTouchListener(multiTouchListener);

      // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
      //           ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      // params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
      parentView.addView(brushDrawingRootView); //, params);
      addedViews.add(brushDrawingRootView);
      if (onPhotoEditorSDKListener != null)
        onPhotoEditorSDKListener.onAddViewListener(ViewType.BRUSH_DRAWING, addedViews.size());
  }

    public void addImage(Bitmap desiredImage) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View imageRootView = inflater.inflate(R.layout.photo_editor_sdk_image_item_list, null);
        ImageView imageView = (ImageView) imageRootView.findViewById(R.id.photo_editor_sdk_image_iv);
        imageView.setImageBitmap(desiredImage);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
                parentView, this.imageView, onPhotoEditorSDKListener);
        multiTouchListener.setOnMultiTouchListener(this);
        imageRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(imageRootView, params);
        addedViews.add(imageRootView);
        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onAddViewListener(ViewType.IMAGE, addedViews.size());
    }

    public void addText(String text, int colorCodeTextView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addTextRootView = inflater.inflate(R.layout.photo_editor_sdk_text_item_list, null);
        TextView addTextView = (TextView) addTextRootView.findViewById(R.id.photo_editor_sdk_text_tv);
        addTextView.setGravity(Gravity.CENTER);
        addTextView.setText(text);
        // addTextView.setEnabled = false;
        if (colorCodeTextView != -1)
            addTextView.setTextColor(colorCodeTextView);
        MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
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

    public void addEmoji(String emojiName, Typeface emojiFont) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emojiRootView = inflater.inflate(R.layout.photo_editor_sdk_text_item_list, null);
        TextView emojiTextView = (TextView) emojiRootView.findViewById(R.id.photo_editor_sdk_text_tv);
        emojiTextView.setTypeface(emojiFont);
        emojiTextView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        emojiTextView.setText(convertEmoji(emojiName));
        MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
                parentView, this.imageView, onPhotoEditorSDKListener);
        multiTouchListener.setOnMultiTouchListener(this);
        emojiRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(emojiRootView, params);
        addedViews.add(emojiRootView);
        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onAddViewListener(ViewType.EMOJI, addedViews.size());
    }
    // VIEWS ADDING -- end

    // SETTERS -- start
    public void setBrushDrawingMode(boolean brushDrawingMode) {
        // if (brushDrawingView != null)
        //     brushDrawingView.setBrushDrawingMode(brushDrawingMode);

        for (View view : addedViews) {
          if (view instanceof BrushDrawingView) {
            ((BrushDrawingView) view).setBrushDrawingMode(brushDrawingMode);
          }
        }
    }

    public void setBrushSize(float size) {
        // if (brushDrawingView != null)
        //      .setBrushSize(size);

        for (View view : addedViews) {
          if (view instanceof BrushDrawingView) {
            ((BrushDrawingView) view).setBrushSize(size);
          }
        }
    }

    public void setBrushColor(@ColorInt int color) {
        // if (brushDrawingView != null)
        //     brushDrawingView.setBrushColor(color);

        for (View view : addedViews) {
          if (view instanceof BrushDrawingView) {
            ((BrushDrawingView) view).setBrushColor(color);
          }
        }
    }

    public void setBrushEraserSize(float brushEraserSize) {
        // if (brushDrawingView != null)
        //     brushDrawingView.setBrushEraserSize(brushEraserSize);

        for (View view : addedViews) {
          if (view instanceof BrushDrawingView) {
            ((BrushDrawingView) view).setBrushEraserSize(brushEraserSize);
          }
        }
    }

    public void setBrushEraserColor(@ColorInt int color) {
        // if (brushDrawingView != null)
        //     brushDrawingView.setBrushEraserColor(color);

        for (View view : addedViews) {
          if (view instanceof BrushDrawingView) {
            ((BrushDrawingView) view).setBrushEraserColor(color);
          }
        }
    }
    // SETTERS -- end

    // GETTER -- start
    //  TODO: replace view settings getters with common setting from js props
    // should we use brush getters instead of using 
    public float getEraserSize() {
        // if (brushDrawingView != null)
        //     return brushDrawingView.getEraserSize();
        // return 0;

        return 15;
    }

    public float getBrushSize() {
        // if (brushDrawingView != null)
        //     return brushDrawingView.getBrushSize();
        // return 0;

        return 15;
    }

    public int getBrushColor() {
        // if (brushDrawingView != null)
        //     return brushDrawingView.getBrushColor();
        // return 0;

        return 15;
    }
    // GETTER -- end

    public void brushEraser() {
        // if (brushDrawingView != null)
        //     brushDrawingView.brushEraser();
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

    public void clearBrushAllViews() {
        // if (brushDrawingView != null)
        //     brushDrawingView.clearAll();
    }

    public void clearAllViews() {
        for (int i = 0; i < addedViews.size(); i++) {
            parentView.removeView(addedViews.get(i));
        }
        // if (brushDrawingView != null)
        //     brushDrawingView.clearAll();
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
            Log.d("PhotoEditorSDK", "selected camera path " + selectedOutputPath);
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

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void setOnPhotoEditorSDKListener(OnPhotoEditorSDKListener onPhotoEditorSDKListener) {
        this.onPhotoEditorSDKListener = onPhotoEditorSDKListener;

        // brushDrawingView.setOnPhotoEditorSDKListener(onPhotoEditorSDKListener);
        brushDrawingView.setOnPhotoEditorSDKListener(onPhotoEditorSDKListener); // test
    }

    @Override
    public void onEditTextClickListener(String text, int colorCode) {
        if (addTextRootView != null) {
            parentView.removeView(addTextRootView);
            addedViews.remove(addTextRootView);
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
        private View deleteView;
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

        public PhotoEditorSDKBuilder deleteView(View deleteView) {
            this.deleteView = deleteView;
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
