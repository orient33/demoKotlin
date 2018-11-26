package com.example.kotlindemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_test.*

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
        rv.addItemDecoration(Divider(4))

        val lis: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.d(
                    "df", "first :" + lm.findFirstVisibleItemPosition()
                            + ". last: " + lm.findLastVisibleItemPosition()
                )
            }
        }
        Log.d("df", "add lis")
        rv.addOnScrollListener(lis)
    }

    internal class Adapter : RecyclerView.Adapter<VH>() {
        private val list = mutableListOf<Int>()
        fun setData(d: List<Int>) {
            list.clear()
            list.addAll(d)
            notifyDataSetChanged()
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