package com.example

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.util.*

/**
 * @author dundongfang on 2018/9/25.
 */

fun log(msg: String, tag: String = "df") {
    Log.d(tag, msg)
}

fun toast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, duration).show()
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