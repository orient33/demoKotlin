package com.example.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.UserHandle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.window.layout.WindowMetricsCalculator
import com.example.StatusBarTool
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
        StatusBarTool.adaptSysBar(view)
        adapter = CommonAdapter()
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if (context == null) return
        val view = activity?.window?.decorView
        val dm = requireContext().resources?.displayMetrics!!
        val displayValue =
            "density = ${dm.density}, densityDpi = ${dm.densityDpi}, scaledDensity=${dm.scaledDensity}," +
                    "widthPx=${dm.widthPixels}, heightPx=${dm.heightPixels}, xdpi=${dm.xdpi},ydpi=${dm.ydpi}" +
                    " '160dp' is '${(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        160f,
                        dm
                    ))}px'"

        val wm = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(requireActivity())
        val screenWidth = wm.bounds.width()
        val screenHeight = wm.bounds.height()

        val physicalX = screenWidth / dm.xdpi
        val physicalY = screenHeight / dm.ydpi

        val wm2 = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(requireActivity())
        val screenValue = "屏幕信息androidx.window.bounds = ${wm.bounds},  swWidth=${screenWidth / dm.density}," +
                    " sHeight = ${screenHeight / dm.density} , physical= ($physicalX $physicalY),,当前(小窗)${wm2.bounds}"
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
                    Log.w("df", "invoke oaid fail.$e")
                }
            }
        val vaid =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                "null"
            } else {
                try {
                    val clz: Class<*> =
                        Class.forName("com.android.id.IdentifierManager")
                    val ins: Any = clz.newInstance()
                    val oaidMethod: Method = clz.getMethod("getVAID", Context::class.java)
                    oaidMethod.invoke(ins, context)
                } catch (e: Exception) {
                    Log.w("df", "invoke oaid fail.$e")
                }
            }
        val uid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            "${Process.myUserHandle()}, ${
                UserHandle.getUserHandleForUid(-2).hashCode()
            }, uid=${Process.myUid()}"
        } else {
            "-1000"
        }


        val am = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val lowMem = am.isLowRamDevice
        val list = listOf(displayValue, screenValue, displayCutout, "oaid=$oaid , vaid=$vaid", "低内存设备=$lowMem","uid=$uid")
        Log.w("df", "oaid= $oaid , vaid= $vaid")
        adapter.setData(list)
    }

}