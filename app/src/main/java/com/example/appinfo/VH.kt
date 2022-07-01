package com.example.appinfo

import android.annotation.SuppressLint
import android.content.pm.LauncherActivityInfo
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import com.example.appName
import com.example.kotlindemo.R

class VH internal constructor(v: View) : RecyclerView.ViewHolder(v) {
    private val icon: ImageView = v.findViewById(R.id.icon)
    private val name: TextView = v.findViewById(R.id.name)
    private val cn: TextView = v.findViewById(R.id.cn)

    private val densityDpi: Int
        get() {
            return itemView.context.resources.displayMetrics.densityDpi
        }

    @SuppressLint("SetTextI18n")
    fun bindPackageInfo(pos:Int, listener: View.OnClickListener?, info: PackageInfo) {
        val title = info.applicationInfo.packageName
        val d = info.applicationInfo.loadIcon(itemView.context.packageManager)
        icon.setImageDrawable(d)
        name.text = "${1 + pos}) ${info.appName}"
        cn.text = "$title \n targetSdk: ${info.applicationInfo.targetSdkVersion}"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cn.append(", minSdk: ${info.applicationInfo.minSdkVersion}, uid:${info.applicationInfo.uid},vCode:${info.versionCode},vName:${info.versionName}")
        }
        itemView.tag = title
        itemView.setOnClickListener(listener)
    }

    @SuppressLint("SetTextI18n")
    fun bindLauncherInfo(pos: Int, listener: View.OnClickListener, info: LauncherActivityInfo) {
        val drawable: Drawable = info.getIcon(densityDpi)
        val cnn: String = info.getComponentName().toShortString()
        icon.setImageDrawable(drawable)
        name.text = (1 + pos).toString() + ")  " + info.getLabel()
        cn.text = cnn
        itemView.setOnClickListener(listener)
    }
}