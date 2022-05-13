package ui.photoeditor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.NoSuchKeyException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;


public class ImageSourceConverter {
  static EditedImageSource getImageSource(Context context, ReadableMap source) {
    return new EditedImageSource(context, source.getString("uri"), getHeaders(source));
  }

  static Headers getHeaders(ReadableMap source) {
    Headers headers = Headers.DEFAULT;

    if (source.hasKey("headers")) {
      ReadableMap headersMap = source.getMap("headers");
      ReadableMapKeySetIterator iterator = headersMap.keySetIterator();
      LazyHeaders.Builder builder = new LazyHeaders.Builder();

      while (iterator.hasNextKey()) {
        String header = iterator.nextKey();
        String value = headersMap.getString(header);

        builder.addHeader(header, value);
      }

      headers = builder.build();
    }
    return headers;
  }
}
