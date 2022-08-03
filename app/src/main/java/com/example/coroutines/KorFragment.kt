package com.example.coroutines

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kotlindemo.R

class KorFragment : Fragment(), View.OnClickListener {
    lateinit var vm: KorVM
    var editText: EditText? = null
    var textView: TextView? = null
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
        view.findViewById<Button>(R.id.button2).setOnClickListener(this)
        textView = view.findViewById(R.id.text)
        editText = view.findViewById(R.id.editText)
        vm.getText().observe(viewLifecycleOwner) {
            textView?.text = "WallpaperInfo: $it"
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button) {
            vm.login(editText?.editableText.toString())
        } else {
            requireActivity().startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
        }
    }
}