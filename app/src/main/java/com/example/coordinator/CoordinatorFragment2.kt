package com.example.coordinator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.StatusBarTool
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentCoordinator2Binding

//学习自 https://www.jianshu.com/p/640f4ef05fb2
class CoordinatorFragment2 : Fragment() {
    private var binding2: FragmentCoordinator2Binding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { //这个 layout 可以是2, 3,,这里随机一下..
        binding2 = FragmentCoordinator2Binding.inflate(inflater)
        return binding2!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding2 = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        StatusBarTool.adaptSysBar(view)
//        toolbar.navigationIcon = null
//        toolbar.setLogo(R.drawable.ic_launcher_foreground)
        val toolbar = binding2!!.toolbar
        toolbar.title = "Coordinator Demo"
        toolbar.subtitle = "Coordinator AppBarLayout"
        toolbar.setOnClickListener { switchVp() }

        val pager2 = binding2!!.vp2
        pager2.isUserInputEnabled = false
        pager2.adapter = object : RecyclerView.Adapter<RvHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvHolder {
                return RvHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false)
                )
            }

            override fun onBindViewHolder(holder: RvHolder, position: Int) {
                bindRecyclerView(holder.rv, position)
            }

            override fun getItemCount() = 3
        }
    }

    fun switchVp() {
        val p2 = binding2!!.vp2
        if (p2.currentItem == 0) {
            p2.setCurrentItem(1, false)
        } else {
            p2.setCurrentItem(0, false)
        }
    }

    fun bindRecyclerView(listView: RecyclerView, page: Int) {
        listView.layoutManager =
            LinearLayoutManager(listView.context, LinearLayoutManager.VERTICAL, false)
        listView.adapter = object : RecyclerView.Adapter<VH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return VH(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: VH, position: Int) {
                holder.text.text = "$page, position:$position"
            }

            override fun getItemCount(): Int {
                return 121
            }
        }
    }

    class RvHolder(v: View) : RecyclerView.ViewHolder(v) {
        val rv = v as RecyclerView
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val text = v as TextView
    }
}