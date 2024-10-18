package com.example.tab

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.StatusBarTool
import com.example.kotlindemo.R

class TabFragment() : Fragment(R.layout.fragment_tab) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        StatusBarTool.adaptSysBar(view)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager2)
        /*
        viewPager.adapter =
            object : FragmentPagerAdapter(requireActivity().supportFragmentManager) {
                override fun getCount(): Int = 8

                override fun getItem(position: Int): Fragment {
                    log("getItem $position")
                    return DeviceInfo()
                }

            }
         */
        viewPager.adapter = ViewPager2Adapter()
        viewPager.setPageTransformer(PageTrans1())
        //一屏多页
        val recyclerView: View = viewPager.getChildAt(0)
        if (recyclerView is RecyclerView) {
            recyclerView.setPadding(100, 0, 100, 0)
            recyclerView.clipToPadding = false
        }
    }

    class ViewPager2Adapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_full_text, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.itemView.setBackgroundColor(
                when (position % 3) {
                    0 -> Color.RED
                    1 -> Color.BLUE
                    else -> Color.GREEN
                }
            )
            holder.tv.text = "$position"
        }

        override fun getItemCount() = 5
    }

    class MyViewHolder(rootView: View) :
        RecyclerView.ViewHolder(rootView) {
        val tv = rootView as TextView
    }
}