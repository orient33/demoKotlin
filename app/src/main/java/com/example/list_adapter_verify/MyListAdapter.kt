package com.example.list_adapter_verify

import android.annotation.SuppressLint
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.*
import com.example.kotlindemo.ListFragment
import com.example.kotlindemo.R

class MyListAdapter constructor() :
    ListAdapter<Pair<Int, String>, ListFragment.VH>(
        object : DiffUtil.ItemCallback<Pair<Int, String>>() {
            override fun areItemsTheSame(a: Pair<Int, String>, b: Pair<Int, String>): Boolean {
                return a.first == b.first
            }

            override fun areContentsTheSame(a: Pair<Int, String>, b: Pair<Int, String>): Boolean {
                return a == b
            }

            override fun getChangePayload(oldItem: Pair<Int, String>, newItem: Pair<Int, String>): Any? {
                return 1
            }
        }) {
    init {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                log("onChanged.", "MyListAdapter")
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                log("on inserted.$positionStart,count=$itemCount", "MyListAdapter")
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                log("on changed.$positionStart,count=$itemCount, pay=$payload", "MyListAdapter")
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ListFragment.VH {
        return ListFragment.VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item, parent, false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListFragment.VH, position: Int) {
        holder.text.text = getItem(position).toString()
        log("onBindViewHolder pos = $position", "MyListAdapter")
    }

    fun commitList(list: List<Pair<Int, String>>?) {
        log("submitList: $list")
        submitList(list)
    }
}
