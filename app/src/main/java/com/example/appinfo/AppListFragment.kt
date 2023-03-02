package com.example.appinfo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlindemo.IActivity
import com.example.kotlindemo.R
import com.example.toast

// https://github.com/orient33/getAppInfo
class AppListFragment : Fragment() {
    var root: View? = null
    var mRecyclerView: RecyclerView? = null
    var mSpinner: Spinner? = null
    private var mContext: Context? = null
    private lateinit var vm :PkgListVM
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        vm = ViewModelProvider(this)[PkgListVM::class.java]
    }

    fun <T : View?> findViewById(@IdRes id: Int): T {
        return root!!.findViewById(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_applist, container, false)
        mSpinner = findViewById<Spinner>(R.id.spinner)
        mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val aa = AppAdapter(requireContext())
        vm.loadLauncherApps(true).observe(viewLifecycleOwner){
            aa.setList(it)
        }
        mRecyclerView!!.layoutManager = LinearLayoutManager(root!!.context)
        mRecyclerView!!.adapter = aa
        mSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                vm.doFilterForLauncher(position)
                toast(parent.context, "onItemSelected=$position", 0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                toast(parent.context, "onNothingSelected", 0)
            }
        }
        return root
    }

    internal inner class AppAdapter(val context: Context) :
        RecyclerView.Adapter<VH>(), View.OnClickListener {
        val data: MutableList<LauncherActivityInfo> = ArrayList()

        fun setList(list:List<LauncherActivityInfo>){
            data.clear()
            data.addAll(list)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context).inflate(R.layout.item_appinfo, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: VH, position: Int) {
            val info = data[position]
            holder.bindLauncherInfo(position, this, info)
            holder.itemView.tag = info
        }

        override fun onClick(v: View) {
            toDetail(v.tag as LauncherActivityInfo)
        }
    }

    private fun toDetail(info: LauncherActivityInfo) {
        MyPalette.sLauncherActivityInfo = info
        val act: Activity? = activity
        if (act is IActivity) {
            val ia = act as IActivity
            val fragment = AppInfoFragment()
            ia.toFragment(fragment)
        } else {
            Toast.makeText(act, "can not to page. ", Toast.LENGTH_SHORT).show()
        }
    }
}