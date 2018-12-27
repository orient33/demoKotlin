package com.example.kotlindemo

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.dialog.DialogActivity
import kotlinx.android.synthetic.main.fragment_test.*
import java.util.*

/**
 * @author dundongfang on 2018/4/26.
 */
class ListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    val data = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5)
    //10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = Adapter()
        adapter.setData(data)
//        val lm = VPLL(view.context)
        val lm =
//            GridLayoutManager(view.context, 4)
            LinearLayoutManager(
                view.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )//ScaleLayoutManager(view.context, 10)
        lm.orientation = LinearLayoutManager.HORIZONTAL
//        lm.infinite = true
        rv.layoutManager = lm
        rv.adapter = adapter
        rv.addItemDecoration(Divider())
        val ith = ItemTouchHelper(ItemMoveCallback().setAdapter(adapter))
        ith.attachToRecyclerView(rv)

        Log.d("df", "add lis")
//        rv.addOnScrollListener(lis)
//        val i = Intent()
//        i.setClassName(rv.context, DialogActivity::class.java.name)
//        startActivity(i)
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