package com.example.imagetest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import retrofit2.Call

class ImageVM : ViewModel() {

    private lateinit var list: MutableLiveData<List<ImageData>>
    private val callList = mutableListOf<Call<HomeResponse>>()

    fun getImageData(
        local: Boolean,
        ignoreGif: Boolean,
        pageSize: Int?,
        page: String?
    ): MutableLiveData<List<ImageData>> {
        if (!::list.isInitialized) {
            list = MutableLiveData()
            if (local) {
                loadData()
            } else {
                loadNet(page ?: "HYBRID", pageSize, ignoreGif)
            }
        }
        return list
    }


    override fun onCleared() {
        for (call in callList) {
            call.cancel()
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val l = Repo.loadLocalImage()
            list.postValue(l)
        }
    }

    private fun loadNet(page: String, pageSize: Int?, ignoreGif: Boolean, start: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            val ll = Repo.loadNetImage(page, ignoreGif)
            val current = list.value
            if (current == null || current.isNullOrEmpty()) {
                list.postValue(ll)
            } else {
                val append = mutableListOf<ImageData>()
                append.addAll(current.toSet())
                append.addAll(ll.toImmutableList())
                list.postValue(append)
            }
            val size = pageSize ?: 1
            if (start < size) {
                loadNet(page, pageSize, ignoreGif, 1 + start)
            }
        }
    }
}
