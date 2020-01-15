package com.example.kotlindemo

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.text.TextUtils
import android.util.Log
import com.example.log

//参考 https://juejin.im/post/5a320ffcf265da43200342a3
class ItemMoveCallback : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.DOWN or ItemTouchHelper.UP,
    0
) {
    val edge = 8
    var adapter: SwapAdapter? = null
    fun setAdapter(a: SwapAdapter): ItemMoveCallback {
        adapter = a
        return this
    }

    var lastMove = ""
    override fun onMove(v: androidx.recyclerview.widget.RecyclerView, h1: androidx.recyclerview.widget.RecyclerView.ViewHolder, h2: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
        val move = when {
            h1.adapterPosition > edge -> false
            h2.adapterPosition > edge -> false
            else -> {
                val start = Math.min(h1.adapterPosition, h2.adapterPosition)
                val end = Math.max(h1.adapterPosition, h2.adapterPosition)
                val v = start.toString() + "," + end.toString()
                if (TextUtils.equals(lastMove, v)) {
                    return false
                } else {
                    log("move  $start $end")
                    lastMove = v
                }
                adapter?.swap(start, end)
                true
            }
        }
        Log.d("df", "move ${h1.adapterPosition} -> ${h2.adapterPosition} : $move ")
        return move
    }

    override fun getMovementFlags(recyclerView: androidx.recyclerview.widget.RecyclerView, h: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
        return when {
            h.adapterPosition > edge -> ItemTouchHelper.Callback.makeMovementFlags(0, 0)
            h.adapterPosition == edge -> ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.START, 0)
            else -> ItemTouchHelper.Callback.makeMovementFlags(
                ItemTouchHelper.START or ItemTouchHelper.END
                        or ItemTouchHelper.DOWN or ItemTouchHelper.UP, 0
            )
        }
    }

    //当长按 item 刚开始拖曳的时候调用
    override fun onSelectedChanged(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        log(
            "onSelectChanged. op position: ${viewHolder?.adapterPosition}" +
                    " ${(when (actionState) {
                        0 -> "ACTION_STATE_IDLE"
                        1 -> "ACTION_STATE_SWIPE"
                        2 -> "ACTION_STATE_DRAG"
                        else -> "UNKNOWN"
                    })}"
        )
    }

    //当完成拖曳手指松开的时候调用
    override fun clearView(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        log("clearView. ${viewHolder.adapterPosition}")
    }

    override fun canDropOver(
        recyclerView: androidx.recyclerview.widget.RecyclerView,
        current: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        target: androidx.recyclerview.widget.RecyclerView.ViewHolder
    ): Boolean {
//        log("can drop over ${current.adapterPosition} ${target.adapterPosition}")
        return target.adapterPosition <= edge
    }

    override fun onSwiped(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p: Int) {
        log("onSwiped. $p")
    }
}