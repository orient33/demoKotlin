package com.example

import com.example.RemoveDup.firstNonAZ
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

object MyTest {
    @JvmStatic
    fun main(args: Array<String>) {
//        val now = Calendar.getInstance();
//        Calendar.DAY_OF_YEAR;
//        mylog(" $now, ${now.get(Calendar.DAY_OF_YEAR)}")
//        val job = GlobalScope.launch {
//            delay(1000)
//            mylog("协程 done! " + Thread.currentThread().id)
//        }
//        mylog("main ..hello ," + Thread.currentThread().id)

//        val dimenList = RunShell.runCmd("find ", arrayOf(THEME_ROOT, " -name ", "dimen*.xml.txt"))
//        val list = dimenList.split("\n").filter { it.endsWith("xml.txt") }
//        list.forEach {
//            File(it).deleteOnExit()
//        }
        autoDensity2()
//        readLog()
//        RemoveDup.removeDupDrawable()
//        RemoveDup.removeDupDimen()
//        RemoveDup.renamePngWebp()
//        RemoveDup.removeDupColor()

//        compareFile()
//        Thread.sleep(1888)
//        threadTest(false)
//        threadTest(true)
    }

    //[drawable-xhdpi-v4/retry_n] /home/dun/code.mi/ThemeManager/app/src/main/res/drawable-xhdpi/retry_n.9.png
// [drawable-xhdpi-v4/retry_n] /home/dun/code.mi/ThemeManager/module_recommend/src/main/res/drawable-xhdpi/retry_n.9.png
// : Resource and asset merger: Duplicate resources
    private fun readLog() {
        val logFile = File("/home/dun/code.open/demoKotlin/lib/theme2.txt")
        val map = mutableMapOf<String, Int>()
        logFile.readLines().forEachIndexed { index, s ->
            if (s.startsWith('[')) {
                val key = s.substring(1, firstNonAZ(s))
                val old = map[key]
                if (old == null) {
                    map[key] = 1
                } else {
                    map[key] = old + 1
                }
            } else {
//                throw IllegalArgumentException("line $index , value : $s ")
            }
        }
        var count = 0
        map.forEach { (s, i) ->
            println("$s size $i")
            count += i
        }
        println("count is $count")
    }


    //对比2个文件中重复的dimen定义
    private fun compareFile() {
        val mainFile =
            File("/home/dun/code.mi/ThemeManager/app/src/main/res/values-sw392dp-xxhdpi/dimens.xml")
        val phoneFile =
            File("/home/dun/code.mi/ThemeManager/app/src/phone/res/values-sw392dp-xxhdpi/dimens.xml")
        val mainSet = getSetForFile(mainFile)
        val phoneSet = getSetForFile(phoneFile)

        val dupName = mutableSetOf<String>()
        mainSet.forEach {
            if (phoneSet.contains(it)) {
                dupName.add(it)
                mylog("dup: $it")
            }
        }
        mylog("mainSet ${mainSet.size}, phoneSet ${phoneSet.size},,,dup size ${dupName.size}")
    }

    private fun getSetForFile(mainFile: File): Set<String> {
        val set = mutableSetOf<String>()
        mainFile.readLines().forEach {
            if (it.contains("dimen")) {
                val start = it.indexOf("\"")
                val end = it.lastIndexOf("\"")
                val name = it.substring(start + 1, end)
                set.add(name)
            }
        }
        return set
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