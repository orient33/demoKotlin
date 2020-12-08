package com.example.imagetest

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindemo.R

class PlaceVH(root: View) : RecyclerView.ViewHolder(root) {
    private val nameView = root.findViewById<TextView>(R.id.title)

    @SuppressLint("SetTextI18n")
    fun bind(pos: Int) {
        nameView?.text = "占位item, $pos\n为了瀑布流!"
    }

}