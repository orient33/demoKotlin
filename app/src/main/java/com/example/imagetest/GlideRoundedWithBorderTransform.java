package com.example.imagetest;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class GlideRoundedWithBorderTransform extends BitmapTransformation {

    private static final String ID = GlideRoundedWithBorderTransform.class.getName();
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private final float mRadius;

    private final float mBorderSize;

    private final int mBorderColor;

    GlideRoundedWithBorderTransform() {
        this(36, 2, 0xffff0000);
    }

    GlideRoundedWithBorderTransform(float radius, float borderSize, int borderColor) {
        mRadius = radius;
        mBorderSize = borderSize;
        mBorderColor = borderColor;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool bitmapPool, @NonNull Bitmap bitmap, int targetWidth, int targetHeight) {
        Bitmap result = bitmapPool.get(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint roundPaint = new Paint();
        roundPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        roundPaint.setAntiAlias(true);
        RectF roundRectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(roundRectF, mRadius, mRadius, roundPaint);

        if (mBorderSize > 0) {
            Paint borderPaint = new Paint();
            borderPaint.setColor(mBorderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(mBorderSize);
            borderPaint.setAntiAlias(true);
            RectF borderRectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawRoundRect(borderRectF, mRadius, mRadius, borderPaint);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode()) +
                Util.hashCode(mRadius) +
                Util.hashCode(mBorderSize) +
                Util.hashCode(mBorderColor);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GlideRoundedWithBorderTransform) {
            GlideRoundedWithBorderTransform other = (GlideRoundedWithBorderTransform) obj;
            return mRadius == other.mRadius && mBorderSize == other.mBorderSize && mBorderColor == other.mBorderColor;
        }
        return super.equals(obj);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        byte[] bytes = ByteBuffer.allocate(12)
                .putFloat(mRadius)
                .putFloat(mBorderSize)
                .putInt(mBorderColor)
                .array();
        messageDigest.update(bytes);
    }
}
