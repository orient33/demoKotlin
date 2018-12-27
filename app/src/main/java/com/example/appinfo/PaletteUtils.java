package com.example.appinfo;

import android.graphics.Bitmap;

public class PaletteUtils {
    /**
     * 图片缩放的尺寸
     */
    private static final int CALCULATE_BITMAP_MIN_DIMENSION = 100;

    static Bitmap scaleBitmapDown(Bitmap bitmap) {
        final int minDimension = Math.min(bitmap.getWidth(), bitmap.getHeight());

        if (minDimension <= CALCULATE_BITMAP_MIN_DIMENSION) {
            // If the bitmap is small enough already, just return it
            return bitmap;
        }

        final float scaleRatio = CALCULATE_BITMAP_MIN_DIMENSION / (float) minDimension;
        return Bitmap.createScaledBitmap(bitmap,
                Math.round(bitmap.getWidth() * scaleRatio),
                Math.round(bitmap.getHeight() * scaleRatio),
                false);
    }
}
