package com.example.coordinator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout

class Behavior(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<TextView>(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout, child: TextView,
        dependency: View
    ): Boolean {
        return true
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout, child: TextView,
        dependency: View
    ): Boolean {
        child.x = dependency.x
        child.y = dependency.y + dependency.measuredHeight
        return true
    }
}
