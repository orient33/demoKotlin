package com.example.coordinator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.kotlindemo.R

//学习自 https://www.jianshu.com/p/640f4ef05fb2
class CoordinatorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_coordinator, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btn).setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                v.x = event.rawX - v.width / 2
                v.y = event.rawY - v.height / 2
            }
            true
        }
    }

}