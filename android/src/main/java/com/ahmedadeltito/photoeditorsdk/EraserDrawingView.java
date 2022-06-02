package com.ahmedadeltito.photoeditorsdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * Created by Ahmed Adel on 5/8/17.
 */

public class EraserDrawingView extends View {

  private float eraserSize = 80;

  private Path drawPath;
  private Paint drawPaint;
  private Paint maskPaint;

  private Canvas maskCanvas;
  private Canvas bufferCanvas;
  private Bitmap maskBitmap;
  private Bitmap bufferBitmap;
  private boolean eraserDrawMode;
  private Bitmap backgroundImage;
  private final Paint mPaintSrcIn = new Paint();

  private OnPhotoEditorSDKListener onPhotoEditorSDKListener;

  public EraserDrawingView(Context context) {
    this(context, null);
  }

  public EraserDrawingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setupEraserDrawing();
  }

  public EraserDrawingView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setupEraserDrawing();
  }

  public void setEraserDrawingMode(boolean eraserDrawMode) {
    this.eraserDrawMode = eraserDrawMode;
    if (eraserDrawMode) {
      this.setVisibility(View.VISIBLE);
      refreshEraserDrawing();
    }
  }

  public void setBackgroundImage(Bitmap backgroundImage) {
    this.backgroundImage = backgroundImage;
  }

  public Bitmap getImageBitmap() {
    return this.bufferBitmap.copy(bufferBitmap.getConfig(), bufferBitmap.isMutable());
  }

  void setupEraserDrawing() {
    drawPath = new Path();
    drawPaint = new Paint();
    drawPaint.setAntiAlias(true);
    drawPaint.setDither(true);
    drawPaint.setColor(Color.WHITE);
    drawPaint.setStyle(Paint.Style.STROKE);
    drawPaint.setStrokeJoin(Paint.Join.ROUND);
    drawPaint.setStrokeCap(Paint.Cap.ROUND);
    drawPaint.setStrokeWidth(eraserSize);
    drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
    maskPaint = new Paint(Paint.DITHER_FLAG);
    mPaintSrcIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    this.setVisibility(View.GONE);
  }

  private void refreshEraserDrawing() {
    eraserDrawMode = true;
    drawPaint.setAntiAlias(true);
    drawPaint.setDither(true);
    drawPaint.setStyle(Paint.Style.STROKE);
    drawPaint.setStrokeJoin(Paint.Join.ROUND);
    drawPaint.setStrokeCap(Paint.Cap.ROUND);
    drawPaint.setStrokeWidth(eraserSize);
    drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
  }


  void setEraserSize(float size) {
    eraserSize = size;
    refreshEraserDrawing();
  }

  public void clearAll() {
    if (maskCanvas != null) {
      maskCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
      bufferCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
      redrawBufferCanvas();
      invalidate();
    }
  }

  public void setOnPhotoEditorSDKListener(OnPhotoEditorSDKListener onPhotoEditorSDKListener) {
    this.onPhotoEditorSDKListener = onPhotoEditorSDKListener;
  }


  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    maskCanvas = new Canvas(maskBitmap);
    bufferCanvas = new Canvas(bufferBitmap);
    redrawBufferCanvas();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (maskBitmap != null) {
      maskCanvas.drawBitmap(maskBitmap, 0, 0, maskPaint);
      maskCanvas.drawPath(drawPath, drawPaint);
      redrawBufferCanvas();
    }
  }

  private void redrawBufferCanvas() {
    if (backgroundImage == null || maskBitmap == null || bufferCanvas == null) {
      return;
    }
    bufferCanvas.drawBitmap(maskBitmap, 0, 0, null);
    Rect imageRect = new Rect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
    Rect maskRect = new Rect(0, 0, maskBitmap.getWidth(), maskBitmap.getHeight());
    bufferCanvas.drawBitmap(
        backgroundImage,
        imageRect,
        maskRect,
        mPaintSrcIn);
//    bufferCanvas.drawBitmap(backgroundImage, 0, 0, mPaintSrcIn);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    if (bufferBitmap != null)
      canvas.drawBitmap(bufferBitmap, 0, 0, null);
  }

  @Override
  public boolean onTouchEvent(@NonNull MotionEvent event) {
    if (eraserDrawMode) {
      float touchX = event.getX();
      float touchY = event.getY();
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          drawPath.moveTo(touchX, touchY);
          if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onStartViewChangeListener(ViewType.ERASER_DRAWING);
          break;
        case MotionEvent.ACTION_MOVE:
          drawPath.lineTo(touchX, touchY);
          break;
        case MotionEvent.ACTION_UP:
          maskCanvas.drawPath(drawPath, drawPaint);
          drawPath.reset();
          if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onStopViewChangeListener(ViewType.ERASER_DRAWING);
          break;
        default:
          return false;
      }
      invalidate();
      return true;
    } else {
      return false;
    }
  }
}
