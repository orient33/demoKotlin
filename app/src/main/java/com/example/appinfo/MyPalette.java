package com.example.appinfo;

import android.annotation.SuppressLint;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.example.kotlindemo.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyPalette {

    static LauncherActivityInfo sLauncherActivityInfo;
    private static final int COLOR_DEFAULT = 0x80FFFFFF;

    static ArrayList<Palette.Swatch> COLORES = new ArrayList<>(7);

    static void init() {
        if (COLORES.size() > 0) return;
        /* UI人员提供的7种颜色, (不含默认的白色 0xF7F9FD) */
        int[] colors = {
//                0x8023c5a8, 0x805e43da, 0x80fac230, 0x80ff8900, 0x804a90e2, 0x80ef4848, 0x8046c14b
                0xff36D5B9, 0xff7E63F9, 0xffFBC332, 0xffF99929, 0xff4A90E2, 0xffF35F5F, 0xff42CC8E
        };
        for (int color : colors) {
            COLORES.add(new Palette.Swatch(color, 0));
        }
    }

    /**
     * Generate and return a color synchronously.
     */
    static int getColor(Bitmap bitmap, ArrayList<Palette.Swatch> listSwatch) {

        Palette.Filter f = new Palette.Filter() {

            private boolean isBlack(float[] hslColor) {
                return hslColor[2] <= 0.1f;
            }

            private boolean isWhite(float[] hslColor) {
                return hslColor[2] >= 0.9f;
            }

            private boolean isNearRedILine(float[] hslColor) {
                return hslColor[0] >= 10f && hslColor[0] <= 37f && hslColor[1] <= 0.82f;
            }

            @Override
            public boolean isAllowed(int rgb, @NonNull float[] hsl) {
                return !isWhite(hsl) && !isBlack(hsl) && !isNearRedILine(hsl);
            }
        };
        Palette p = Palette.from(bitmap)
                .clearFilters()
//                .addFilter(f)
//                .resizeBitmapSize(400)
                .generate();
        List<Palette.Swatch> list = p.getSwatches();
        listSwatch.addAll(list);
        Collections.sort(listSwatch, (l, r) -> l.getPopulation() - r.getPopulation());
        Palette.Swatch swatch = listSwatch.get(0);
        // find most population Swatch.
        for (int i = 1; i < listSwatch.size(); ++i) {
            Palette.Swatch si = listSwatch.get(i);
            if (si == null) {
                continue;
            }
            if (swatch == null || swatch.getPopulation() < si.getPopulation()) {
                swatch = si;
            }
        }

        if (swatch != null) {
            return findNearColor(swatch.getRgb());
        }
        return COLOR_DEFAULT;
    }

    private static int findNearColor(int color) {
        if (COLORES.size() < 1)
            init();
        int len = COLORES.size();
        float hsl[] = new float[3];
        ColorUtils.colorToHSL(color, hsl);
        float distance[] = new float[len];
        for (int i = 0; i < len; ++i) {
            distance[i] = getDistance(COLORES.get(i).getHsl(), hsl);
        }
        float min = Float.MAX_VALUE;
        int resultIndex = 0;
        for (int i = 0; i < len; ++i) {
            if (distance[i] < min) {
                resultIndex = i;
                min = distance[i];
            }
        }
        return COLORES.get(resultIndex).getRgb();
    }

    private static float getDistance(float a[], float b[]) {
        return calculate(a[0], b[0]) + calculate(a[1], b[1]) + calculate(a[2], b[2]);
    }

    private static float calculate(float a, float b) {
        return (a - b) * (a - b);
    }

    public static void findThemeColor(Drawable icon, final TextView view, final String label) {
        @SuppressLint("SetTextI18n") Palette.PaletteAsyncListener listener = p -> {
            Palette.Swatch swatch;
            if (p == null) return;
            if ((swatch = p.getVibrantSwatch()) != null) {
                view.setBackgroundColor(swatch.getRgb());
                view.setText(label + "-Vibrant");
                view.setTextColor(swatch.getBodyTextColor());
            } else if ((swatch = p.getLightVibrantSwatch()) != null) {
                view.setBackgroundColor(swatch.getRgb());
                view.setText(label + "-LightVibrant");
                view.setTextColor(swatch.getBodyTextColor());
            } else if ((swatch = p.getDarkVibrantSwatch()) != null) {
                view.setBackgroundColor(swatch.getRgb());
                view.setText(label + "-DarkVibrant");
                view.setTextColor(swatch.getBodyTextColor());
            } else if ((swatch = p.getMutedSwatch()) != null) {
                view.setBackgroundColor(swatch.getRgb());
                view.setText(label + "-Muted");
                view.setTextColor(swatch.getBodyTextColor());
            } else if ((swatch = p.getLightMutedSwatch()) != null) {
                view.setBackgroundColor(swatch.getRgb());
                view.setText(label + "-LightMuted");
                view.setTextColor(swatch.getBodyTextColor());
            } else if ((swatch = p.getDarkMutedSwatch()) != null) {
                view.setBackgroundColor(swatch.getRgb());
                view.setTextColor(swatch.getBodyTextColor());
                view.setText(label + "-DarkMuted");
            } else {
                view.setText(label + "- null -");
                view.setBackgroundColor(Color.GRAY);
                Log.e("dd", "can not find a color ." + view.getText());
            }
            // cache.put(label, swatch == null ? NULL_SWATCH : swatch);
        };
        Log.i("dd", "find/generate  for " + label);
        Bitmap bitmap = Utils.getBitmapFromDrawable(icon);
        try {
            Palette.from(bitmap).generate(listener);
        } catch (IllegalArgumentException e) {
            Toast.makeText(view.getContext(), label + ": " + e, Toast.LENGTH_SHORT).show();
        }
    }

}