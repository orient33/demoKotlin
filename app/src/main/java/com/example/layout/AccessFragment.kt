package com.example.layout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SpeechTool
import com.example.StatusBarTool
import com.example.TAG
import com.example.kotlindemo.ACTION
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentAccessBinding
import com.example.kotlindemo.databinding.FragmentSBinding
import kotlinx.coroutines.launch

class AccessFragment : androidx.fragment.app.Fragment(), View.OnClickListener {
    private var _binding: FragmentAccessBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccessBinding.inflate(inflater)
        binding.start.setOnClickListener(this)
        binding.end.setOnClickListener(this)
        binding.quick.setOnClickListener(this)
        val vm = ViewModelProvider(this)[ViewModelA::class.java]
        vm.listen(binding.output)
        StatusBarTool.adaptSysBar(binding.root)
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.quick -> openQuickSet()
            R.id.start -> SpeechTool.startS(v.context.applicationContext)
            R.id.end -> SpeechTool.stop(v.context.applicationContext)
        }
    }

    private fun openQuickSet() {
        // GLOBAL_ACTION_QUICK_SETTINGS
        val ctx = requireContext()
        Log.i(TAG, "open quick set.!  broadcast action ")
        ctx.sendBroadcast(Intent(ACTION))
    }
}

@SuppressLint("SetTextI18n")
class ViewModelA : ViewModel() {
    fun listen(v: TextView) {
        viewModelScope.launch {
            v.text = "out"
            SpeechTool.getOutFlow().collect {
                v.append(it)
            }
        }
    }
}