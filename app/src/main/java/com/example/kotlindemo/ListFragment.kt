package com.example.kotlindemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.list2String
import com.example.log
import com.example.toast

/**
 * @author dundongfang on 2018/4/26.
 */
@Keep
class ListFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val rv1 = view.findViewById<RecyclerView>(R.id.rv1)
        val text1 = view.findViewById<View>(R.id.text1)
        setupRecyclerView(view.context, rv)
        setupRecyclerView(view.context, rv1)
        text1.setOnClickListener { v ->
            run {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "audio/*"
                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/*", "application/ogg"))
                if (null != intent.resolveActivity(v.context.packageManager)) {
                    v.context.startActivity(intent)
                } else {
                    toast(v.context, "can not find Activity for $intent")
                }
                Utils.sendNotification(
                    v.context,
                    "text+ " + System.currentTimeMillis(),
                    "start audio."
                )
            }
        }
    }

    private fun setupRecyclerView(context: Context, v: androidx.recyclerview.widget.RecyclerView) {
        val adapter = Adapter(25)
        val lm = androidx.recyclerview.widget.LinearLayoutManager(
            context,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        v.layoutManager = lm
        v.adapter = adapter
        v.addItemDecoration(Divider())
        ItemTouchHelper(ItemMoveCallback().setAdapter(adapter))
            .attachToRecyclerView(v)
    }

    class Adapter(size: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<VH>(), SwapAdapter {
        private val list = mutableListOf<Int>()

        init {
            for (i in 0..size) {
                list.add(i)
            }
            notifyDataSetChanged()
        }

        fun setData(d: List<Int>) {
            list.clear()
            list.addAll(d)
            notifyDataSetChanged()
        }

        override fun swap(a: Int, b: Int) {
            log(a.toString() + " before swap:" + list2String(list))
//            moveItem(a, b, list)
            list.add(b, list.removeAt(a))
//            Collections.swap(list, a, b)
            notifyItemMoved(a, b)
            log(b.toString() + " after swap:" + list2String(list))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
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