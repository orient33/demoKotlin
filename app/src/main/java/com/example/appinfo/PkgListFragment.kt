package com.example.appinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appName
import com.example.kotlindemo.R
import com.example.toast
import kotlinx.android.synthetic.main.fragment_applist.*

const val FILTER_DATA = 0
const val FILTER_SYS = 1
const val FILTER_ALL = 2
const val FILTER_UPDATE_SYS = 3
class PkgListFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_applist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val la = context?.packageManager?.getInstalledPackages(0)
        val adapter = AAdapter(la)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                adapter.setFilter(position)
                toast(view.context, "onItemSelected=$position", 0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                toast(parent.context, "onNothingSelected", 0)
            }
        }
    }

    internal class AAdapter(var all: List<PackageInfo>?) : RecyclerView.Adapter<VH>(),
        View.OnClickListener {
        val data = mutableListOf<PackageInfo>()
        fun setFilter(filter: Int) {
            val t = all?.filter {
                when (filter) {
                    FILTER_SYS -> {
                        0 != it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM
                    }
                    FILTER_DATA -> {
                        0 == it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM
                    }
                    FILTER_UPDATE_SYS -> {
                        0 != it.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
                    }
                    else -> true
                }
            }
            if (t != null) {
                data.clear()
                data.addAll(t)
                data.sortBy {
                    it.applicationInfo.packageName
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context).inflate(R.layout.item_appinfo, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: VH, position: Int) {
            val info = data[position]
            val title = info.applicationInfo.packageName
            val d = info.applicationInfo.loadIcon(holder.itemView.context.packageManager)
            holder.icon.setImageDrawable(d)
            holder.name.text = "${1 + position}) ${info.appName}"
            holder.cn.text = "$title \n targetSdk: ${info.applicationInfo.targetSdkVersion}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.cn.append(", minSdk: ${info.applicationInfo.minSdkVersion}, uid:${info.applicationInfo.uid}")
            }
            holder.itemView.tag = title
            holder.itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val tag = v.tag
            if (tag is String) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$tag")
                if (intent.resolveActivity(v.context.packageManager) != null) {
                    v.context.startActivity(intent)
                } else {
                    toast(v.context, "click $tag, can not resolve intent!")
                }
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }
}