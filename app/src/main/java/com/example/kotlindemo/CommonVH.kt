package com.example.kotlindemo

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView

class CommonVH(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
    val text = v as TextView
}