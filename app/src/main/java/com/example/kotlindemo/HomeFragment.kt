package com.example.kotlindemo

import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.appinfo.AppListFragment
import com.example.appinfo.PkgListFragment
import com.example.coordinator.CoordinatorFragment2
import com.example.coroutines.KorFragment
import com.example.device.DeviceInfo
import com.example.imagetest.ImageHomeFragment
import com.example.layout.ConstraintFragment
import com.example.list_adapter_verify.ListAdapterTestFragment
import com.example.location.LocationFragment
import com.example.pip.PiPFragment
import com.example.room.RoomFragment
import com.example.screenrecoder.RecorderFragment
import com.example.soundpool.SoundFragment
import com.example.tink.TinkerFragment
import com.example.usb.UsbFragment

/**
 * @author dundongfang on 2018/4/26.
 */
class HomeFragment : androidx.fragment.app.Fragment(), AdapterView.OnItemClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = listOf(
            Pair("图片列表demo-1", ImageHomeFragment::class.java.name),
            Pair("录屏Demo", RecorderFragment::class.java.name),
            Pair("ConstraintFragment", ConstraintFragment::class.java.name),
            Pair("kotlin协程", KorFragment::class.java.name),
            Pair("列表", ListFragment::class.java.name),
            Pair("设备信息", DeviceInfo::class.java.name),
            Pair("ListAdapter测试", ListAdapterTestFragment::class.java.name),
            Pair("SoundPool", SoundFragment::class.java.name),
            Pair("谷歌Tinker加密", TinkerFragment::class.java.name),
            Pair("地理位置", LocationFragment::class.java.name),
            Pair("U盘", UsbFragment::class.java.name),
            Pair("ROOM数据库", RoomFragment::class.java.name),
            Pair("LauncherApp列表", AppListFragment::class.java.name),
            Pair("应用列表", PkgListFragment::class.java.name),
            Pair("Coordinator1", CoordinatorFragment2::class.java.name),
            Pair("画中画", PiPFragment::class.java.name)
        )
        val listView = view.findViewById<ListView>(R.id.listView)
        listView.onItemClickListener = this
        listView.adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val tv = (convertView
                    ?: layoutInflater.inflate(
                        android.R.layout.simple_list_item_1,
                        parent,
                        false
                    )) as TextView
                tv.text = list[position].first
                tv.tag = list[position]
                return tv
            }

            override fun getItem(position: Int): Pair<String, String> {
                return list[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                return list.size
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (view == null) return
        val tag = (view.tag as Pair<*, *>).second
        val activity = requireActivity()
        if (activity is IActivity)
            activity.toFragment(tag as String)
    }
}