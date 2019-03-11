package com.example.kotlindemo;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

public class Divider extends ItemDecoration {

    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//        int pos = parent.getChildAdapterPosition(view);
//        Log.d(TAG, "getItemOffsets:  set rect. " + pos);
        outRect.set(10, 0, 10, 0);
//        outRect.set(0, 0, pos % span == 0 ? 100 : 50, 10);
        //这个设置 对于 网格layout不起作用.. 只对于linear layout manager有效
    }
}