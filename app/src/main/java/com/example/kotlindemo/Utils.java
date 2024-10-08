package com.example.kotlindemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.IconCompat;

import android.util.Log;

import java.io.File;

import android.util.TypedValue;
//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;

import static android.content.Context.NOTIFICATION_SERVICE;

//import com.androidnetworking.AndroidNetworking;
//import com.androidnetworking.common.Priority;
//import com.androidnetworking.error.ANError;
//import com.androidnetworking.interfaces.DownloadListener;
//import com.androidnetworking.interfaces.ParsedRequestListener;

/**
 * @author dundongfang on 2017/12/9.
 */

public class Utils {

//    static {
//        System.loadLibrary("bspatch");
//    }

    //bspatch合并 差分包, (bsdiff的逆过程)
//    public static native void patch(String old, String newApk, String patch);

    //    public static void testLamba() {
//        Flowable<String> f = Flowable.just("a", "b");
//        f.subscribe(s -> android.util.Log.e("df", "accept.. " + s),
//                throwable -> {
//                });
//    }

    @SuppressLint("CheckResult")
    public static void patchAndInstall(Context context, String old, String pPath) {
        final String newApk = context.getExternalFilesDir("apk").getPath() + "/new.apk";
        Log.e("df", "start patch/合并. old :" + old + "\n patch:" + pPath + "\n new:" + newApk);
//        Observable.create(emitter -> {
//            Log.e("df", "patch-before .");
////            patch(old, newApk, pPath);
//            Log.e("df", "patch-after/over/complete.");
//            emitter.onNext(newApk);
//            emitter.onComplete();
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((o) -> install(context, new File(newApk)));
    }

    private static void install(Context c, File f) {
        Log.e("df", "install APK : " + f.getPath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(c, c.getPackageName(), f),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        c.startActivity(intent);
    }

//    public static void checkUpdate(PackageInfo pi, @Nullable String host, String dirPath, DownloadListener dl) {
//        AndroidNetworking.get(TextUtils.isEmpty(host) ? ConstKt.getHOST() : ("http://" + host + ":8888"))
//                .addQueryParameter("pkgName", pi.packageName)
//                .addQueryParameter("verName", pi.versionName)
//                .addQueryParameter("verCode", pi.versionCode + "")
//                .setPriority(Priority.HIGH)
//                .setTag("check")
//                .build().getAsObject(UpdateResult.class, new ParsedRequestListener<UpdateResult>() {
//            @Override
//            public void onResponse(UpdateResult response) {
//                if (response != null && response.isValid()) {
//                    download(host, dirPath, dl);
//                } else {
//                    dl.onError(new ANError("has no update !"));
//                }
//            }
//
//            @Override
//            public void onError(ANError anError) {
//                dl.onError(anError);
//            }
//        });
//
//    }

//    private static void download(@Nullable String host, String dirPath, DownloadListener dl) {
//        AndroidNetworking.download(TextUtils.isEmpty(host) ? ConstKt.getFILE_URL() : ("http://" + host + ":8888/apk"),
//                dirPath, ConstKt.getFILE_NAME())
//                .setTag("downloadTest")
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .startDownload(dl);
//    }

    @NonNull
    public static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public static int dp2px(Context c, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                c.getResources().getDisplayMetrics());
    }

    public static void sendNotification(Context context, String title, String msg) {
        NotificationManager nm = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);
        String channelId = "upgrade";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        String channelName = "升级11";
        if (nm.getNotificationChannel(channelId) == null) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
        }
//        }
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .addAction(new NotificationCompat.Action(
                        IconCompat.createWithResource(context, R.drawable.g8c),
                        "open demo app",
                        PendingIntent.getActivity(context, 1, new Intent(context, MainActivity.class), PendingIntent.FLAG_IMMUTABLE)
                ))
                .build();
        nm.notify(title.hashCode(), notification);
    }
}
