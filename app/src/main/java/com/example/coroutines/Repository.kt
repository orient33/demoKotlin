package com.example.coroutines

import android.os.Process
import com.example.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {

    //suspend 的只能在协程中调用..(协程可以是在UI线程?)
    suspend fun doSomething(): String {
        //
        log("Repository.doSomething.pid=${Process.myPid()} , tid=${Process.myTid()}")

        // Move the execution of the coroutine to the I/O dispatcher
        return withContext(Dispatchers.IO) {
            log("withContext(IO)")
            "resultOnContext"
        }
    }
}