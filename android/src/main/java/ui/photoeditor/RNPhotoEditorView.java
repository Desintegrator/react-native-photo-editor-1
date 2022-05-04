package ui.photoeditor;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

public class RNPhotoEditorView extends FrameLayout {

  public RNPhotoEditorView(@NonNull Context context) {
    super(context);
    this.setBackgroundColor(Color.WHITE);
  }
}
