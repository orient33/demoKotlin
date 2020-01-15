package com.example.device

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlindemo.CommonAdapter
import com.example.kotlindemo.R
import kotlinx.android.synthetic.main.fragment_list2.*

class DeviceInfo : androidx.fragment.app.Fragment() {
    lateinit var adapter: CommonAdapter<String>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CommonAdapter()
        rv.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(context)
        rv.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if (context == null) return
        val view = activity?.window?.decorView
        val dm = context!!.resources?.displayMetrics
        val displayValue =
            "density = ${dm?.density}, densityDpi = ${dm?.densityDpi}, scaledDensity=${dm?.scaledDensity}," +
                    "widthPx=${dm?.widthPixels}, heightPx=${dm?.heightPixels}, xdpi=${dm?.xdpi},ydpi=${dm?.ydpi}" +
                    " '160dp' is '${(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160f, dm))}px'"

        val display = activity?.windowManager?.defaultDisplay
        val point = Point()
        display?.getRealSize(point)
        val screenValue = "screenPx= $point, name=${display?.name}, "
//        DisplayCutout dc
        var displayCutout = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val dc = view?.rootWindowInsets?.displayCutout
            displayCutout = "刘海" + dc.toString()
        }
        val list = listOf(displayValue, screenValue, displayCutout, "")
        adapter.setData(list)
    }

}