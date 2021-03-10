package com.example.device

import android.app.ActivityManager
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindemo.CommonAdapter
import com.example.kotlindemo.R
import java.lang.reflect.Method

class DeviceInfo : Fragment() {
    lateinit var adapter: CommonAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CommonAdapter()
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if (context == null) return
        val view = activity?.window?.decorView
        val dm = requireContext().resources?.displayMetrics
        val displayValue =
            "density = ${dm?.density}, densityDpi = ${dm?.densityDpi}, scaledDensity=${dm?.scaledDensity}," +
                    "widthPx=${dm?.widthPixels}, heightPx=${dm?.heightPixels}, xdpi=${dm?.xdpi},ydpi=${dm?.ydpi}" +
                    " '160dp' is '${(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        160f,
                        dm
                    ))}px'"

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
        val oaid =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                "null"
            } else {
                try {
                    val clz: Class<*> =
                        Class.forName("com.android.id.IdentifierManager")
                    val ins: Any = clz.newInstance()
                    val oaidMethod: Method = clz.getMethod("getOAID", Context::class.java)
                    oaidMethod.invoke(ins, context)
                } catch (e: Exception) {
                    android.util.Log.w("df", "invoke oaid fail.$e")
                }
            }

        val am = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val lowMem = am.isLowRamDevice
        val list = listOf(displayValue, screenValue, displayCutout, "oaid=$oaid", "低内存设备=$lowMem")
        adapter.setData(list)
    }

}