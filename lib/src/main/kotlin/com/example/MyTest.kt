package com.example

import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

object MyTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val now = Calendar.getInstance();
        Calendar.DAY_OF_YEAR;
        mylog(" $now, ${now.get(Calendar.DAY_OF_YEAR)}")
        val job = GlobalScope.launch {
            delay(1000)
            mylog("协程 done! " + Thread.currentThread().id)
        }
        mylog("main ..hello ," + Thread.currentThread().id)
        Thread.sleep(1888)
        threadTest(false)
        threadTest(true)
    }

    private fun threadTest(use: Boolean) {
        val c = AtomicLong()
        for (i in 1..100000L) {
            if (use) {
                GlobalScope.launch {
                    c.addAndGet(i)
                }
            } else {
                thread(start = true) {
                    c.addAndGet(i)
                }
            }
        }
        mylog(c.get().toString())
    }

    private fun mylog(msg: String) {
        println(Date(System.currentTimeMillis()).toString() + ":" + msg)
    }
}