package com.example.kotlindemo

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CommonAdapter<T> : RecyclerView.Adapter<CommonVH>() {
    val list = mutableListOf<T>()
    fun setData(data: List<T>) {
        list.clear()
        for (it in data) {
            if (TextUtils.isEmpty(it?.toString())) {
                continue
            }
            list.add(it)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonVH {
        return CommonVH(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CommonVH, position: Int) {
        val t = list[position]
        holder.text.text = t.toString()
    }
}