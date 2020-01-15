package com.example.coordinator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindemo.R
import kotlinx.android.synthetic.main.fragment_coordinator2.*

//学习自 https://www.jianshu.com/p/640f4ef05fb2
class CoordinatorFragment2 : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? { //这个 layout 可以是2, 3,,这里随机一下..
        val layoutId = if (System.currentTimeMillis().toInt() % 2 == 0)
            R.layout.fragment_coordinator2 else R.layout.fragment_coordinator3
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        toolbar.navigationIcon = null
//        toolbar.setLogo(R.drawable.ic_launcher_foreground)
        toolbar.title = "Coordinator Demo"
        toolbar.subtitle = "Coordinator AppBarLayout"
        listView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
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