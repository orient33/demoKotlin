package com.example

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author dundongfang on 2018/9/25.
 */

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
        return applicationInfo.loadLabel(App.sContext.packageManager).toString()
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