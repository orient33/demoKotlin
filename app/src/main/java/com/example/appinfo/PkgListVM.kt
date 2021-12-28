package com.example.appinfo

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.appName
import com.example.dataStore
import com.example.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PkgListVM(val app: Application) : AndroidViewModel(app) {

    private val mPkgList = MutableLiveData<List<PackageInfo>>()
    private val mConfigFilter = MutableLiveData<Int>()
    private val mConfigKey = MutableLiveData<String>()

    private val mAllList = mutableListOf<PackageInfo>()
    private var mFilter = FILTER_DATA
    private var mKey = ""
    private val dataKeyFilter = intPreferencesKey("pkgList.filter")
    private val dataKeyWords = stringPreferencesKey("pkgList.searchKey")

//    private val wordFlow = app.dataStore.data.map { it[dataKeyWords] ?: "" }
//    private val filterFlow = app.dataStore.data.map { it[dataKeyFilter] ?: 0 }

    fun getConfigFilter(): LiveData<Int> {
        return mConfigFilter
    }

    fun getConfigKey(): LiveData<String> {
        return mConfigKey
    }


    fun getAppsList(force: Boolean = false): LiveData<List<PackageInfo>> {
        if (mPkgList.value == null || force) {
            viewModelScope.launch(Dispatchers.IO) {
                val list = app.packageManager.getInstalledPackages(0)
                mAllList.clear()
                mAllList.addAll(list)
//                wordFlow.collect { mKey = it }
//                filterFlow.collect { mFilter = it }
                //FIXME 这个读取不是正规的 读取方式 需继续
                app.dataStore.edit {
                    mFilter = it[dataKeyFilter] ?: 0
                    mKey = it[dataKeyWords] ?: ""
                }
                mConfigFilter.postValue(mFilter)
                mConfigKey.postValue(mKey)
                postListValue(filterList(mFilter, mKey, list))
            }
        }
        return mPkgList
    }

    fun doFilter(filter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mAllList.size > 0) {
                mFilter = filter
                postListValue(filterList(filter, mKey, mAllList))
                app.dataStore.edit {
                    it[dataKeyFilter] = filter
                }
            }
        }
    }

    fun doQuery(key: String?): LiveData<List<PackageInfo>> {
        if (key != null) {
            viewModelScope.launch(Dispatchers.IO) {
                mKey = key
                mConfigKey.postValue(mKey)
                val list = filterList(mFilter, key, mAllList)
                postListValue(list)
                app.dataStore.edit {
                    it[dataKeyWords] = key
                }
            }
        }
        return mPkgList
    }

    private fun postListValue(list: List<PackageInfo>) {
        //FIXME
        val old = mPkgList.value
        if (old != null && old.size == list.size) {
            log("same size list.. ignore.!")
            return
        }
        mPkgList.postValue(list)
    }

    private fun filterList(filter: Int, key: String, list: List<PackageInfo>): List<PackageInfo> {
//        log("filterList. with filter=$filter, key=$key, allList= ${list.size}")
        val result = mutableListOf<PackageInfo>()
        list.filterTo(result) {
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
        result.sortBy {
            it.applicationInfo.packageName
        }

        return if (key.isNotBlank()) {
            val r = result.filter {
                it.appName.contains(key) or it.packageName.contains(key) or it.versionName.contains(
                    key
                )
            }
            r
        } else {
            result
        }
    }

}