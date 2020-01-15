package com.example.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.App

class UserViewModel : ViewModel() {

    //    private var data: MutableLiveData<List<User>>? = null
//    private val users: MutableLiveData<List<User>>
//        get() {
//            if (data == null) {
//                data = MutableLiveData()
//            }
//            return data
//        }
    private lateinit var users: LiveData<List<User>>

    fun getUsers(): LiveData<List<User>> {
        if (!::users.isInitialized) {
//            users = MutableLiveData<List<User>>()
            users = AppDb.getIns(App.sContext).userDao().getAllLiveData()
        }
        return users
    }
}
