package com.example.appinfo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kotlindemo.R;

public class VH extends RecyclerView.ViewHolder {
    public final ImageView icon;
    public final TextView name, cn;

    VH(View v) {
        super(v);
        icon = v.findViewById(R.id.icon);
        name = v.findViewById(R.id.name);
        cn = v.findViewById(R.id.cn);
    }
}
