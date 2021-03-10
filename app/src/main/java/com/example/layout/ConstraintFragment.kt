package com.example.layout

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentConstraintBinding

class ConstraintFragment : androidx.fragment.app.Fragment(), View.OnClickListener {
    private var _binding : FragmentConstraintBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConstraintBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.start.setOnClickListener(this)
        binding.end.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.start -> {
                val cons = ConstraintSet()
                cons.clone(binding.constraint)
                cons.setGuidelinePercent(R.id.top, 0.6f)
                cons.setGuidelinePercent(R.id.left, 0.1f)
                cons.setGuidelinePercent(R.id.right, 0.9f)
                TransitionManager.beginDelayedTransition(binding.constraint)
                cons.applyTo(binding.constraint)
            }
            R.id.end -> {
                val cons = ConstraintSet()
                cons.clone(binding.constraint)
                cons.setGuidelinePercent(R.id.top, 0.99f)
                cons.setGuidelinePercent(R.id.left, 0.6f)
                cons.setGuidelinePercent(R.id.right, 0.8f)
                TransitionManager.beginDelayedTransition(binding.constraint)
                cons.applyTo(binding.constraint)
            }
        }
    }
}