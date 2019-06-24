package com.example.appinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.appName
import com.example.kotlindemo.R
import com.example.toast
import kotlinx.android.synthetic.main.fragment_applist.*

class PkgListFragment : Fragment() {
    val FILTER_DATA = 0
    val FILTER_SYS = 1
    val FILTER_ALL = 2
    val FILTER_UPDATE_SYS = 3
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_applist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val la = context?.packageManager?.getInstalledPackages(0)
        val adapter = AAdapter(la)
        list_view.adapter = adapter
        list_view.onItemClickListener = AdapterView.OnItemClickListener { _, v, position, _ ->
            run {
                val pi = adapter.getItem(position).applicationInfo.packageName
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$pi")
                if (intent.resolveActivity(v.context.packageManager) != null) {
                    v.context.startActivity(intent)
                } else {
                    toast(v.context, "click $position, can not resolve intent!")
                }
//                Settings.ACTION_App
            }
        }
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

    inner class AAdapter(var all: List<PackageInfo>?) : BaseAdapter() {
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

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            if (view == null) {
                view = View.inflate(context, R.layout.item_pack_info, null)
//                view.setOnClickListener(this)
            }
            val info = getItem(position)
            val title = info.applicationInfo.packageName
            val name = view!!.findViewById<TextView>(R.id.name)
            name.text = "${1 + position}) ${info.appName}"
            val cn = view.findViewById<TextView>(R.id.pkg)
            cn.text = "$title \n targetSdk: ${info.applicationInfo.targetSdkVersion}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cn.append(", minSdk: ${info.applicationInfo.minSdkVersion}, uid:${info.applicationInfo.uid}")
            }

            view.tag = info
            view.setTag(R.id.name, position)
            return view
        }

        override fun getItem(position: Int): PackageInfo {
            return data[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

    }
}