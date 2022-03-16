package com.example.coroutines

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kotlindemo.R

class KorFragment : Fragment(), View.OnClickListener {
    lateinit var vm: KorVM
    var editText: EditText? = null
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
        view.findViewById<Button>(R.id.button).setOnClickListener(this)
        editText = view.findViewById(R.id.editText)
    }

    override fun onClick(v: View) {
        vm.login(editText?.editableText.toString())
    }
}