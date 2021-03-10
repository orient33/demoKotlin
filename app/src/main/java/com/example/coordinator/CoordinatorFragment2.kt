package com.example.coordinator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentCoordinator2Binding
import com.example.kotlindemo.databinding.FragmentCoordinator3Binding

//学习自 https://www.jianshu.com/p/640f4ef05fb2
class CoordinatorFragment2 : Fragment() {
    private var binding2: FragmentCoordinator2Binding? = null
    private var binding3: FragmentCoordinator3Binding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { //这个 layout 可以是2, 3,,这里随机一下..
        return when (System.currentTimeMillis().toInt() % 2) {
            0 -> {
                binding2 = FragmentCoordinator2Binding.inflate(inflater)
                binding2!!.root
            }
            else -> {
                binding3 = FragmentCoordinator3Binding.inflate(inflater)
                binding3!!.root
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding3 = null
        binding2 = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        toolbar.navigationIcon = null
//        toolbar.setLogo(R.drawable.ic_launcher_foreground)
        val toolbar = if (binding3 == null) binding2!!.toolbar else binding3!!.toolbar
        val listView = if (binding3 == null) binding2!!.listView else binding3!!.listView
        toolbar.title = "Coordinator Demo"
        toolbar.subtitle = "Coordinator AppBarLayout"
        listView.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        listView.adapter = object : RecyclerView.Adapter<VH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return VH(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: VH, position: Int) {
                holder.text.text = "position:$position"
            }

            override fun getItemCount(): Int {
                return 21
            }

        }
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val text = v as TextView
    }
}