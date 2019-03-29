package com.example.kotlindemo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class CommonVH(v: View) : RecyclerView.ViewHolder(v) {
    val text = v as TextView
}