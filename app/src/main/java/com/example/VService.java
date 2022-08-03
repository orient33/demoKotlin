package com.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.kotlindemo.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class VService extends WallpaperService {
    private final String TAG = "df";

    @Override
    public Engine onCreateEngine() {
        Log.d(TAG, "onCreateEngine: " + this);
        return new VideoEngine();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        w1 = ResourcesCompat.getDrawable(getResources(), R.drawable.asset, null);
        b1 = BitmapFactory.decodeResource(getResources(), R.raw.gradient49, null);
    }

    Bitmap b1;
    Drawable w1;
    Paint paint = new Paint();

    private class VideoEngine extends WallpaperService.Engine {
        //        @Override
        //        public void onCreate(SurfaceHolder surfaceHolder) {
        //            super.onCreate(surfaceHolder);
        //        }
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Canvas c = holder.getSurface().lockHardwareCanvas();
            // w1.draw(c); //这个绘制 无效.
            c.drawBitmap(b1, 0, 0, paint);
            holder.getSurface().unlockCanvasAndPost(c);
            Log.d(TAG, "onSurfaceCreated: " + holder);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.d(TAG, "visibility : " + visible);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }
}
