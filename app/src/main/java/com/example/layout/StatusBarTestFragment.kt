package com.example.layout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.SpeechTool
import com.example.StatusBarTool
import com.example.TAG
import com.example.kotlindemo.ACTION
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentSBinding

class StatusBarTestFragment : androidx.fragment.app.Fragment(), View.OnClickListener {
    private var _binding : FragmentSBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSBinding.inflate(inflater)
        return binding.root
        // TODO
    }

    private val flag = listOf(
        View.SYSTEM_UI_FLAG_FULLSCREEN,
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,

    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        StatusBarTool.adaptSysBar(view)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.start -> {
            }
            R.id.end -> {
            }
        }
    }
}