package com.example.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.App
import com.example.Injector

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
            val dao =  AppDb.getIns(Injector.sContext).userDao()
            users = dao.getAllLiveData()    //可以在 主线程调用, 因返回值是LiveData<?>
//            val ee = dao.get("111")       //不能在主线程调
//            val e = dao.getAll()          //不能在主线程调
        }
        return users
    }
}
