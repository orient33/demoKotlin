package com.example.kotlindemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.displayCut
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

//import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener, IActivity {

    lateinit var fm: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayCut(this)
        setContentView(R.layout.activity_main)
        val list = listOf(
            Pair("录屏Demo", "com.example.screenrecoder.RecorderFragment"),
            Pair("临时测试页", "com.example.kotlindemo.TestFragment"),
            Pair("列表", "com.example.kotlindemo.ListFragment"),
            Pair("列表2", "com.example.kotlindemo.ListFragment2"),
            Pair("设备信息", "com.example.device.DeviceInfo"),
            Pair("ListAdapter测试", "com.example.list_adapter_verify.ListAdapterTestFragment"),
            Pair("SoundPool", "com.example.soundpool.SoundFragment"),
            Pair("谷歌Tink加密", "com.example.tink.TinkFragment"),
            Pair("地理位置", "com.example.location.LocationFragment"),
            Pair("U盘", "com.example.usb.UsbFragment"),
            Pair("ROOM数据库", "com.example.room.RoomFragment"),
            Pair("LauncherApp列表", "com.example.appinfo.AppListFragment"),
            Pair("应用列表", "com.example.appinfo.PkgListFragment"),
            Pair("画中画", "com.example.pip.PiPFragment")
        )
        listView.onItemClickListener = this
        fm = supportFragmentManager
        listView.adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val tv = (convertView
                    ?: layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)) as TextView
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
//        setContentView(R.layout.activity_main)
//        val pi = packageManager.getPackageInfo(packageName, 0)
//        val ai = packageManager.getApplicationInfo(packageName, 0)
//        textView.text = getString(R.string.version_info, pi.versionName, pi.versionCode, ai.targetSdkVersion)
//        button.setOnClickListener {
//            Log.e("df", "click. id= =")
//        }
//        button2.text = if (pi.versionCode == 1) "抛出异常.FC...player" else "已修复FC !"
//        button2.setOnClickListener {
//            Log.e("df", "just throw Exception.")
//            if (pi.versionCode == 1) throw RuntimeException(" throw a Exception..!")
//        }
//        val list = listOf("abcdefg", "bcde")
//        list.toObservable()
//                .filter { it.length >= 5 }
//                .subscribeBy(
//                        onNext = { log("onNext() " + it) },
//                        onError = { log("onError() " + it) }
//                )
//        log("onCreate")
//
//        //kotlin标准库提供的with
//        val p = Person(10, "dongfang")
//        with(p) {
//            log("age is $age , name is $name, height is $gao")
//        }

        val current = Date(System.currentTimeMillis())
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK)
        df.timeZone = SimpleTimeZone(0, "UTC")
        val time = df.format(current)

        val date = df.parse("2018-08-14 09:00:00")

        log("current time : $time, $current,  parse date $date") //2018-08-01 13:00:00
// current time : 2018-08-14 10:24:34, Tue Aug 14 18:24:34 GMT+08:00 2018,  parse date Tue Aug 14 17:00:00 GMT+08:00 2018
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (view == null) return
        val tag = (view.tag as Pair<*, *>).second
        val fragment = Fragment.instantiate(this, tag as String)
        toFragment(fragment)
    }

    override fun toFragment(fragment: Fragment) {
        fm.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_right,
                R.anim.slide_in_right, R.anim.slide_out_right
            )
            .add(R.id.container, fragment)
            .addToBackStack(fragment.toString())
            .commit()
    }

    override fun onBackPressed() {
        val f = fm.findFragmentById(R.id.container)
        if (f is IFragment) {
            if (!f.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun log(msg: String) {
        android.util.Log.e("df", "Demo.MultiAppActivity] $msg")
//        msg.javaClass
//        msg::class.java
//        msg::class.javaObjectType
//        msg::class.javaPrimitiveType
    }
}
