package com.example.imagetest

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindemo.R

class ImageAdapter(
    context: Context,
    private val resetHeight: Boolean,
    private val roundImage: Boolean,
    private val cardView: Boolean,
    private val stag: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: List<ImageData> = listOf()
    private val inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val id = if (cardView) R.layout.item_image2 else R.layout.item_image1
        return when (viewType) {
            0 -> ImageVH(inflater.inflate(id, parent, false))
            else -> PlaceVH(inflater.inflate(R.layout.item_placeholder, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (stag && position == 0) {
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageVH)
            holder.bind(position, list[position], resetHeight, roundImage && !cardView)
        else if (holder is PlaceVH)
            holder.bind(position)
    }

    override fun getItemCount(): Int = list.size

    fun setData(data: List<ImageData>) {
        list = data
        notifyDataSetChanged()
    }
}