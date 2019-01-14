package com.example.kotlindemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_test.*
import java.util.*

/**
 * @author dundongfang on 2018/4/26.
 */
class ListFragment2 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list2, container, false)
    }

    val data = listOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9,// 10, 11, 12, 13, 14, 15,
        10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = Adapter()
        adapter.setData(data)
        val lm = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        rv.layoutManager = lm
        rv.adapter = adapter
        rv.addItemDecoration(Divider())
    }

    class Adapter : RecyclerView.Adapter<VH>() {
        private val list = mutableListOf<Int>()
        fun setData(d: List<Int>) {
            list.clear()
            list.addAll(d)
            notifyDataSetChanged()
        }

        fun swap(a: Int, b: Int) {
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