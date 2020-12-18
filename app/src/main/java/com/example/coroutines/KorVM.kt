package com.example.coroutines

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

//主线程安全: 如果在主线程执行,不会导致 阻塞界面更新
//协程与线程关系 如同 线程与进程关系
class KorVM(
) : ViewModel() {
    var job: Job? = null
    fun login() {
        // Create a new coroutine to move the execution off the UI thread
        //如果不加 IO 则会在UI线程执行 协程
        log("KorVM.login. 111111111")
        val repository = Repository()
        job = viewModelScope.launch(Dispatchers.IO) {
            //do network request.
            log("KorVM.login.launch. 1")
            SystemClock.sleep(1111)
            log("KorVM.login.launch. 2")
            SystemClock.sleep(1111)
            log("KorVM.login.launch. 3")
            SystemClock.sleep(1111)
            log("KorVM.login.launch. 4")
            SystemClock.sleep(11111)
            val value = try {
                repository.doSomething()
            } catch (e: Exception) {
                "doSomeThing.error"
            }
            log("KorVM. result = $value")
        }
    }

    override fun onCleared() {
        job?.cancel()   //cancel并不能取消正在运行的代码块.
        log("KorVM. onCleared.")
    }
}