package com.example.kotlindemo

import android.os.Bundle
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_test.*
import java.util.*

/**
 * 竖向的RecyclerView. 外层套了一个下拉控件 SwipeRefreshLayout.
 * @author dundongfang on 2018/4/26.
 */
@Keep
class ListFragment2 : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = Adapter()
        adapter.setData()
        val lm = androidx.recyclerview.widget.LinearLayoutManager(
            view.context,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )
        rv.layoutManager = lm
        rv.adapter = adapter
        rv.addItemDecoration(Divider())
        ItemTouchHelper(ItemMoveCallback().setAdapter(adapter)).attachToRecyclerView(rv)
    }

    class Adapter : androidx.recyclerview.widget.RecyclerView.Adapter<VH>(), SwapAdapter {
        private val list = mutableListOf<Int>()
        fun setData() {
            for (i in 0..20) {
                list.add(i)
            }
            notifyDataSetChanged()
        }

        override fun swap(a: Int, b: Int) {
            Collections.swap(list, a, b)
            notifyItemMoved(a, b)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.item,
                parent, false
            )
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.text.text = list[position].toString()
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    class VH(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val text: TextView = v as TextView
    }

}