package com.example.kotlindemo;

import android.annotation.TargetApi;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

@TargetApi(21)
public class RecognizeBgDrawable extends Drawable {
    private final Paint paint = new Paint();
    private final Path path = new Path();

    public RecognizeBgDrawable(int color) {
        paint.setColor(color);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Rect r = getBounds();
        int radius = (r.bottom - r.top) / 2;
        Log.v("df", "draw. rect is = " + r);//0, 0  240, 50)
        path.reset();
        path.moveTo(r.right - radius, r.top);
        path.arcTo(r.right - 2 * radius, r.top, r.right, r.bottom,
                270f, 180f, true);
        path.lineTo(r.left, r.bottom);
        path.arcTo(r.left - radius, r.top, r.left + +radius, r.bottom,
                90f, -180f, true);
        path.lineTo(r.right - radius, r.top);
        path.close();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
