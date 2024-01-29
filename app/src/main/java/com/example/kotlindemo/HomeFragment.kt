package com.example.kotlindemo

import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MD3Activity
import com.example.RvClickListener
import com.example.a13.PhotoPickerDemo
import com.example.appinfo.LauncherAppListFragment
import com.example.appinfo.PkgListFragment
import com.example.coordinator.CoordinatorFragment2
import com.example.coroutines.KorFragment
import com.example.device.DeviceInfo
import com.example.imagetest.ImageHomeFragment
import com.example.imagetest.ImageMatrixFragment
import com.example.layout.ConstraintFragment
import com.example.list_adapter_verify.ListAdapterTestFragment
import com.example.location.LocationFragment
import com.example.media.MediaFragment
import com.example.pip.PiPFragment
import com.example.room.RoomFragment
import com.example.screenrecoder.RecorderFragment
import com.example.soundpool.SoundFragment
import com.example.tab.TabFragment
import com.example.tink.TinkerFragment
import com.example.usb.UsbFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * @author dundongfang on 2018/4/26.
 */
class HomeFragment : androidx.fragment.app.Fragment(R.layout.fragment_home) {

    var mList: List<Pair<String, String>>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mList = listOf(
            Pair("图片列表demo-1", ImageHomeFragment::class.java.name),
            Pair("录屏Demo", RecorderFragment::class.java.name),
            Pair("ConstraintFragment", ConstraintFragment::class.java.name),
            Pair("kotlin协程", KorFragment::class.java.name),
            Pair("列表", ListFragment::class.java.name),
            Pair("TabFragment", TabFragment::class.java.name),
            Pair("设备信息", DeviceInfo::class.java.name),
            Pair("ListAdapter测试", ListAdapterTestFragment::class.java.name),
            Pair("SoundPool", SoundFragment::class.java.name),
            Pair("谷歌Tinker加密", TinkerFragment::class.java.name),
            Pair("地理位置", LocationFragment::class.java.name),
            Pair("U盘", UsbFragment::class.java.name),
            Pair("ROOM数据库", RoomFragment::class.java.name),
            Pair("LauncherApp列表", LauncherAppListFragment::class.java.name),
            Pair("应用列表", PkgListFragment::class.java.name),
            Pair("图片缩放", ImageMatrixFragment::class.java.name),
            Pair("Coordinator1", CoordinatorFragment2::class.java.name),
            Pair("MediaCompat", MediaFragment::class.java.name),
            Pair("画中画", PiPFragment::class.java.name)
        )
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab?.let { fabBtn ->
            fabBtn.setOnClickListener {
                startActivity(
                    Intent(
                        it.context,
                        MD3Activity::class.java
                    )
                )
            }
            fabBtn.setOnLongClickListener {
                PhotoPickerDemo.startPhotoPickerSingle(this, requireActivity())
                true
            }
        }
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(view.context)
        rv.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
        rv.adapter = CommonAdapter<String>().apply {
            setData(mList!!.map { it.first })
        }
        rv.addOnItemTouchListener(object : RvClickListener(rv) {
            override fun onItemClick(vh: RecyclerView.ViewHolder, v: View) {
                val list = mList ?: return
                val position = vh.bindingAdapterPosition
                if (position < 0) return
                val tag = list[position].second
                val activity = requireActivity()
                if (activity is IActivity)
                    activity.toFragment(tag as String)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PhotoPickerDemo.onPhotoResult(requireActivity() as MainActivity, requestCode, resultCode, data)
    }
}