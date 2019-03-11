package com.example.kotlindemo

import android.os.Bundle
import android.support.annotation.Keep
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
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
class ListFragment2 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = Adapter()
        adapter.setData()
        val lm = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = lm
        rv.adapter = adapter
        rv.addItemDecoration(Divider())
        ItemTouchHelper(ItemMoveCallback().setAdapter(adapter)).attachToRecyclerView(rv)
    }

    class Adapter : RecyclerView.Adapter<VH>(), SwapAdapter {
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

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v as TextView
    }

}