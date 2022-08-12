package com.example

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import androidx.core.view.GestureDetectorCompat
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View

abstract class RvClickListener(val recyclerView: RecyclerView) : SimpleOnItemTouchListener() {
    private val mGestureDetectorCompat: GestureDetectorCompat

    init {
        mGestureDetectorCompat = GestureDetectorCompat(
            recyclerView.context, MyGestureListener()
        )
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        mGestureDetectorCompat.onTouchEvent(e)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        mGestureDetectorCompat.onTouchEvent(e)
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    abstract fun onItemClick(vh: RecyclerView.ViewHolder, v: View)

    private inner class MyGestureListener : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val childView = recyclerView.findChildViewUnder(e.x, e.y)
            if (childView != null) {
                val viewHolder = recyclerView.getChildViewHolder(childView)
                onItemClick(viewHolder, childView) //触发回调
            }
            return true
        }
    }

}