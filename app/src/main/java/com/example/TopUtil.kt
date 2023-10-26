package com.example

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author dundongfang on 2018/9/25.
 */
//https://developer.android.google.cn/topic/libraries/architecture/datastore?hl=zh-cn
// 用于取代 SharedPreference的数据存储
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun log(msg: String, tag: String = "df") {
    Log.i(tag, msg)
}

fun toast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, duration).show()
}

fun isMainThread(): Boolean {
    return Looper.getMainLooper() == Looper.myLooper()
}

fun formatTimeNow() = formatTime(System.currentTimeMillis())
fun formatTime(time: Long): String {
    val yy = "yyyy-MM-dd HH:mm:ss"
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val ldt = LocalDateTime.now()//(LocalTime.now())
        val dtf = DateTimeFormatter.ofPattern(yy)
        ldt.format(dtf)
    } else {
        val sdf = SimpleDateFormat(yy, Locale.CHINA)
        sdf.format(Date(time))
    }
}

fun <T> list2String(list: List<T>?): String {
    return when {
        list == null -> "list.null"
        list.isEmpty() -> "list.empty"
        else -> {
            val sb = StringBuilder("list size(" + list.size + "):")
            for (t in list) {
                sb.append(t.toString()).append(",")
            }
            sb.toString()
        }
    }
}

fun <T> moveItem(sourceIndex: Int, targetIndex: Int, list: List<T>) {
    if (sourceIndex <= targetIndex) {
        Collections.rotate(list.subList(sourceIndex, targetIndex + 1), -1)
    } else {
        Collections.rotate(list.subList(targetIndex, sourceIndex + 1), 1)
    }
}

fun displayCut(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val attr = activity.window.attributes
        attr.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        activity.window.attributes = attr
    }
}

val PackageInfo.appName: String
    get() {
        return applicationInfo.loadLabel(Injector.sContext.packageManager).toString()
    }

fun drawableToString(d: Drawable): String {
    return when (d) {
        is AdaptiveIconDrawable -> {
            var text =
                "分层图标 AdaptiveIconDrawable, size=${d.intrinsicWidth}px\n" +
                        " 背景 size= ${d.background.intrinsicWidth}, ${drawableToString(d.background)}\n" +
                        " 前景 size=${d.foreground.intrinsicWidth}, ${drawableToString(d.foreground)}\n"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val mono = d.monochrome
                if (mono != null) {
                    text += "单色图层 size = ${mono.intrinsicWidth} , ${drawableToString(mono)}"
                }
            }
            text
        }

        is BitmapDrawable -> {
            "单层图标 ${d.intrinsicWidth}x${d.intrinsicHeight}px. bitmap ${d.bitmap.width}x${d.bitmap.height},density=${d.bitmap.density} "
        }

        is LayerDrawable -> {
            val size = d.numberOfLayers
            val sb = StringBuilder()
            for (i in 0 until size) {
                val c = d.getDrawable(i)
                sb.append("$i, ${drawableToString(c)}\n")
            }
            "LayerDrawable. $size: \n$sb"
        }

        is ColorDrawable -> "纯颜色 color=0x${Integer.toHexString(d.color)}"
        is VectorDrawable -> "矢量图 ${d.javaClass.simpleName}"
        else -> d.javaClass.toString()
    }
}

fun <T> invoke(
    method: Method,
    instance: Any,
    vararg args: Any
): T? {
    var result: T? = null
    try {
        method.isAccessible = true
        result = method.invoke(instance, *args) as T
    } catch (e: Exception) {
        log("invoke Exception: $e")
    }
    return result
}

fun atLeast(api: Int) = Build.VERSION.SDK_INT >= api
/**
 * Extensions
 *
 * Created by huzenan on 2017/8/3.
 */
//internal fun HashMap<View, EasyPullLayout.ChildViewAttr>.getByType(type: Int?): View? {
//    for ((key) in this)
//        if ((key.layoutParams as EasyPullLayout.LayoutParams).type == type)
//            return key
//    return null
//}