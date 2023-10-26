package com.example.appinfo

import android.annotation.SuppressLint
import android.content.pm.LauncherActivityInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appName
import com.example.kotlindemo.R

class VH internal constructor(v: View) : RecyclerView.ViewHolder(v) {
    private val icon: ImageView = v.findViewById(R.id.icon)
    private val name: TextView = v.findViewById(R.id.name)
    private val cn: TextView = v.findViewById(R.id.cn)

    @SuppressLint("SetTextI18n")
    fun bindPackageInfo(pos: Int, listener: View.OnClickListener, longClick:View.OnLongClickListener, info: PackageInfo) {
        val title = info.applicationInfo.packageName
        val d = info.applicationInfo.loadIcon(itemView.context.packageManager)
        icon.setImageDrawable(d)
        name.text = "${1 + pos}) ${info.appName}, ${d.javaClass.simpleName}"
        cn.text = "$title \n targetSdk: ${info.applicationInfo.targetSdkVersion}"
        cn.append(", minSdk: ${info.applicationInfo.minSdkVersion}, uid:${info.applicationInfo.uid},vCode:${info.versionCode},vName:${info.versionName}")
        itemView.tag = info
        itemView.setOnClickListener(listener)
        itemView.setOnLongClickListener(longClick)
    }

    @SuppressLint("SetTextI18n")
    fun bindLauncherInfo(pos: Int, listener: View.OnClickListener, longClick:View.OnLongClickListener,  info: LauncherActivityInfo) {
        val drawable: Drawable = info.getIcon(0)
        val cnn: String = info.componentName.toShortString()
        icon.setImageDrawable(drawable)
        name.text = (1 + pos).toString() + ")  " + info.label
        cn.text = cnn + "\n" + drawable.javaClass.simpleName
        itemView.setOnClickListener(listener)
        itemView.setOnLongClickListener(longClick)
    }
}