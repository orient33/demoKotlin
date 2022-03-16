package com.example.tab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.device.DeviceInfo
import com.example.kotlindemo.R
import com.example.log

class TabFragment() : Fragment(R.layout.fragment_tab) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter =
            object : FragmentPagerAdapter(requireActivity().supportFragmentManager) {
                override fun getCount(): Int = 8

                override fun getItem(position: Int): Fragment {
                    log("getItem $position")
                    return DeviceInfo()
                }

            }
    }
}