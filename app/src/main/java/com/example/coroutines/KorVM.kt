package com.example.coroutines

import android.app.Application
import android.app.WallpaperManager
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlindemo.R
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
class KorVM(val app: Application) : AndroidViewModel(app) {
    var job: Job? = null
    fun login(delay: String?) {
        // Create a new coroutine to move the execution off the UI thread
        //如果不加 IO 则会在UI线程执行 协程
        log("KorVM.login. 111111111")
        val repository = Repository()
        job = viewModelScope.launch(Dispatchers.IO) {
            //do network request.
            log("KorVM.login.launch. 1")
            SystemClock.sleep(1000)
            log("KorVM.login.launch. 2")
            SystemClock.sleep(1000)
            log("KorVM.login.launch. 3")
            SystemClock.sleep(1000)
            val addDelay = if (!delay.isNullOrEmpty()) delay.toInt() else 0
            if (addDelay > 0) {
                log("sleep .$addDelay s")
                SystemClock.sleep(addDelay * 1000L)
            }
            val id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                WallpaperManager.getInstance(app)
                    .setResource(R.raw.wallpaper, WallpaperManager.FLAG_SYSTEM)
            } else {
                -100
            }
            log("KorVM.login.launch. 4 .set wallpaper id = $id")
            SystemClock.sleep(111)
            val ii = Intent("ACTION_DOWNLOAD_CLICK_NOTIFICATIONcom.android.thememanager")
            ii.putExtra("EXTRA_ACTIVITY_TARGET_INTENT", Intent().apply {
                setClassName(
                    "com.android.contacts",
                    "com.android.contacts.activities.TwelveKeyDialer"
                )
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            app.sendBroadcast(ii)
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