package com.example.appinfo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.TopUtilKt;
import com.example.kotlindemo.R;
import com.example.kotlindemo.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

@TargetApi(21)
public class AppInfoFragment extends Fragment {
    //    public static final String Key = "file";
    View mRoot;
    LauncherActivityInfo mInfo;
    TextView mResult;
    Drawable mIcon;
    Bitmap mBitmap;
    LinearLayout mPaletteColorContainer;

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mRoot.findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_app_info, container, false);
        MyPalette.init();
        mInfo = MyPalette.sLauncherActivityInfo;
        MyPalette.sLauncherActivityInfo = null;
        mIcon = mInfo.getBadgedIcon(getResources().getDisplayMetrics().densityDpi);
        mBitmap = Utils.getBitmapFromDrawable(mIcon);//AdaptiveIconDrawable

        mResult = findViewById(R.id.result);
        TextView appInfo = findViewById(R.id.app_name);
        mPaletteColorContainer = findViewById(R.id.palette_container);
        appInfo.setText(mInfo.getLabel());
        appInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(mIcon, null, null, null);
        LinearLayout swatchContainer = findViewById(R.id.swatch_container);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        for (Palette.Swatch s : MyPalette.COLORES) {
            if (s == null) continue;
            ImageView iv = new ImageView(mContext);
            iv.setBackgroundColor(s.getRgb());
            swatchContainer.addView(iv, params);
        }
        return mRoot;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Palette.Swatch> list = new ArrayList<>(6);
        int finalColor = MyPalette.getColor(mBitmap, list);
        mResult.setBackgroundColor(finalColor);
        mResult.setText(mResult.getText() + " :  0x" + Integer.toHexString(finalColor));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1,
                Utils.dp2px(mResult.getContext(), 40));
        for (Palette.Swatch ss : list) {
            TextView tv = new TextView(mContext);
            tv.setBackgroundColor(ss.getRgb());
            tv.setTextColor(ss.getBodyTextColor());
            tv.setText("Color = 0x" + Integer.toHexString(ss.getRgb())
                    + ",  Population = " + ss.getPopulation());
            mPaletteColorContainer.addView(tv, params);
        }
    }

}
