package com.example.appinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentApplistBinding
import com.example.log
import com.example.toast

const val FILTER_DATA = 0
const val FILTER_SYS = 1
const val FILTER_ALL = 2
const val FILTER_UPDATE_SYS = 3

class PkgListFragment : androidx.fragment.app.Fragment() {
    private var _binding: FragmentApplistBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: PkgListVM
    override fun onAttach(context: Context) {
        super.onAttach(context)
        vm = ViewModelProvider(this)[PkgListVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplistBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val diff = AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<PackageInfo>() {
            override fun areItemsTheSame(oldItem: PackageInfo, newItem: PackageInfo): Boolean {
                return oldItem.packageName == newItem.packageName
            }

            override fun areContentsTheSame(oldItem: PackageInfo, newItem: PackageInfo): Boolean {
                return oldItem.packageName == newItem.packageName
                        && oldItem.lastUpdateTime == newItem.lastUpdateTime
//                      && oldItem.appName == newItem.appName
            }

        }).setBackgroundThreadExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            .build()

        val adapter = AAdapter(diff)
        vm.getAppsList().observe(this) {
            adapter.setAppList(it)
        }
        vm.doQuery(null).observe(this) {
            adapter.setAppList(it)
        }
        vm.getConfigKey().observe(this) {
            if (it != null) {
                binding.searchView.queryHint = it
            }
        }
        vm.getConfigFilter().observe(this) {
            if (it != null && binding.spinner.tag == null) {
                binding.spinner.tag = it
                binding.spinner.setSelection(it)
            }
        }
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                vm.doQuery(newText)
                return true
            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(view.context)
        binding.recyclerView.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                vm.doFilter(position)
                toast(view.context, "onItemSelected=$position", 0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                toast(parent.context, "onNothingSelected", 0)
            }
        }
    }

    internal class AAdapter(diff: AsyncDifferConfig<PackageInfo>) :
        ListAdapter<PackageInfo, VH>(diff), View.OnClickListener {
        fun setAppList(list: List<PackageInfo>) {
            submitList(list)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context).inflate(R.layout.item_appinfo, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: VH, position: Int) {
            val info = getItem(position)
            holder.bindPackageInfo(position, this, info)
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
    }
}