package com.example.layout

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import com.example.kotlindemo.R
import kotlinx.android.synthetic.main.fragment_constraint.*

class ConstraintFragment : androidx.fragment.app.Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_constraint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        start.setOnClickListener(this)
        end.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.start -> {
                val cons = ConstraintSet()
                cons.clone(constraint)
                cons.setGuidelinePercent(R.id.top, 0.6f)
                cons.setGuidelinePercent(R.id.left, 0.1f)
                cons.setGuidelinePercent(R.id.right, 0.9f)
                TransitionManager.beginDelayedTransition(constraint)
                cons.applyTo(constraint)
            }
            R.id.end -> {
                val cons = ConstraintSet()
                cons.clone(constraint)
                cons.setGuidelinePercent(R.id.top, 0.99f)
                cons.setGuidelinePercent(R.id.left, 0.6f)
                cons.setGuidelinePercent(R.id.right, 0.8f)
                TransitionManager.beginDelayedTransition(constraint)
                cons.applyTo(constraint)
            }
        }
    }
}