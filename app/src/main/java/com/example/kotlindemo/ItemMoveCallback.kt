package com.example.kotlindemo

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.example.log

//参考 https://juejin.im/post/5a320ffcf265da43200342a3
class ItemMoveCallback : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.START or ItemTouchHelper.END, 0
) {
    val edge = 4
    var adapter: ListFragment.Adapter? = null
    fun setAdapter(a: ListFragment.Adapter): ItemMoveCallback {
        adapter = a
        return this
    }

    override fun onMove(v: RecyclerView, h1: RecyclerView.ViewHolder, h2: RecyclerView.ViewHolder): Boolean {
        return when {
            h1.adapterPosition > edge -> false
            h2.adapterPosition > edge -> false
            else -> {
                val start = Math.min(h1.adapterPosition, h2.adapterPosition)
                val end = Math.max(h1.adapterPosition, h2.adapterPosition)
                log("move  $start $end")
                adapter?.swap(start, end)
                true
            }
        }
    }

    override fun getMovementFlags(recyclerView: RecyclerView, h: RecyclerView.ViewHolder): Int {
        return when {
            h.adapterPosition > edge -> ItemTouchHelper.Callback.makeMovementFlags(0, 0)
            h.adapterPosition == edge -> ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.START, 0)
            else -> ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.START or ItemTouchHelper.END, 0)
        }
    }

    //当长按 item 刚开始拖曳的时候调用
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        log("onSelectChanged. $actionState ${viewHolder?.adapterPosition}")
    }

    //当完成拖曳手指松开的时候调用
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        log("clearView. ${viewHolder.adapterPosition}")
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        log("can drop over ${current.adapterPosition} ${target.adapterPosition}")
        return target.adapterPosition <= edge
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p: Int) {
        log("onSwiped. $p")
    }
}