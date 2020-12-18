package com.example.coroutines

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kotlindemo.R
import kotlinx.android.synthetic.main.fragment_bind.*

class KorFragment : Fragment(), View.OnClickListener {
    lateinit var vm: KorVM
    override fun onAttach(context: Context) {
        super.onAttach(context)
        vm = ViewModelProvider(this).get(KorVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bind, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        vm.login()
    }
}