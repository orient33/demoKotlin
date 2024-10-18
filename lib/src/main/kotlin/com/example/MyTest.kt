package com.example

import com.example.RemoveDup.firstNonAZ
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.util.Calendar
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

object MyTest {

    val mainScope = CoroutineScope(Dispatchers.Default)
//    val ioScope = CoroutineScope(Dispatchers.IO)

    val sf = flow {
        for (i in 1..3) {
            delay(1111)
            emit(i)
//            mylog("emit sf $i=========================")
        }
    }.stateIn(mainScope, SharingStarted.WhileSubscribed(), 0)


    val wifi = flow {
        for (i in 1..5) {
            delay(1122)
            emit(i % 2 == 0)
//            mylog("emit wifi : ${i % 2 == 0}------------------")
        }
    }.stateIn(mainScope, SharingStarted.WhileSubscribed(), false)

     @JvmStatic
    fun main(args: Array<String>) {
        val now = Calendar.getInstance()
        mylog("start $now, ${now.get(Calendar.DAY_OF_YEAR)}")

        val list2 = listOf(6,5,4)
        mylog("list2 is $list2")
        mylog("list2 sorted: is ${list2.sorted()}")
        mylog("list2 after sorted: is $list2")

        val job = mainScope.launch {
//            sf.combineTransform(wifi) { a, b ->
//                if (a == 3 && b ){
//                    emit("$a $b")
//                }
//            }.collect{
//                mylog(" collect----$it")
//            }
            flow1()

            mylog("协程 done! tid=" + Thread.currentThread().id)
        }

        try {
            do {
                Thread.sleep(1000)
            } while (job.isActive)
            mylog("main ..hello , job : ${job.isCompleted}  . tid=" + Thread.currentThread().id)
        } catch (e: Exception) {
            mylog("error $e")
        }

//        val dimenList = RunShell.runCmd("find ", arrayOf(THEME_ROOT, " -name ", "dimen*.xml.txt"))
//        val list = dimenList.split("\n").filter { it.endsWith("xml.txt") }
//        list.forEach {
//            File(it).deleteOnExit()
//        }
//        autoDensity2()
//        readMemory("thememanager")
//        readLog()
//        RemoveDup.removeDupDrawable()
//        RemoveDup.removeDupDimen()
//        RemoveDup.renamePngWebp()
//        RemoveDup.removeDupColor()

//        compareFile()
//        Thread.sleep(1888)
//        threadTest(false)
//        threadTest(true)
//        compareUrl(
//            "http://dev.market.n.xiaomi.com/thm/page/v3/theme/1abaee12-a1cd-4449-a34d-ca67b4c5a8d6?adCountry=CN&xPrevRef=&language=zh_CN&xRef=&capability=w%2Cb%2Cs%2Cm%2Ch5%2Ca%3A2%2Cv%3A15%2Cvw&isPersonalizedAdEnabled=true&alpha=false&testGroup=main_tab_sort_g1&model=2201123C&packageName=com.android.thememanager&restrictImei=false&miuiUIVersion=V130&apk=3301&oaid=c768b7c39c5e31d7&deviceType=MIPHONE&devicePixel=1080&entryType=thirdapp&isMiuiLiteAndStoke=false&version=12_21.12.29-internal&hwVersion=2.1.2&aodGray=true&system=miui&isGlobal=false&region=CN&device=cupid",
//            "http://dev.market.n.xiaomi.com/thm/page/v3/theme/1abaee12-a1cd-4449-a34d-ca67b4c5a8d6?adCountry=CN&xPrevRef=&language=zh_CN&xRef=LocalTrackId_Page_1a181483-f843-4ad4-9beb-0ccd4ca8f430&capability=w%2Cb%2Cs%2Cm%2Ch5%2Ca%3A2%2Cv%3A15%2Cvw&isPersonalizedAdEnabled=true&alpha=false&testGroup=main_tab_sort_g1&model=M2007J1SC&packageName=com.android.thememanager&restrictImei=false&miuiUIVersion=V130&apk=3110&oaid=67b822dcba56252b&deviceType=MIFOLD&devicePixel=1080&entryType=thememanager&isMiuiLiteAndStoke=false&version=12_V13.0.0.3.SJJCNXM&hwVersion=7.2.0&aodGray=true&system=miui&isGlobal=false&region=CN&device=cas&networkType=-1"
//        )

    }

    //[drawable-xhdpi-v4/retry_n] /home/dun/code.mi/ThemeManager/app/src/main/res/drawable-xhdpi/retry_n.9.png
// [drawable-xhdpi-v4/retry_n] /home/dun/code.mi/ThemeManager/module_recommend/src/main/res/drawable-xhdpi/retry_n.9.png
// : Resource and asset merger: Duplicate resources

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun flow1() {
        val flowA = flowOf(1, 2, 3, 4, 5, 6).onEach { delay(122) }
        val flowB = flowOf(false, true, false, false).onEach {
            delay(300)
        }//.stateIn(myScope)

        combine(flowA, flowB
        ){a, b ->
            "$a+ $b"
        }.collect{
            mylog("collect $it")
        }
//        flowA.transformLatest {
//            if (it == 3) {
//                delay(2000)
//                emit(it)
//            } else {
//                emit(it)
//            }
//        }
//            .collect {
//                mylog("1: collect $it")
//            }
    }

    @OptIn(FlowPreview::class)
    private suspend fun flow2() {
        var sent4 = false
        (1..5).asFlow()
            .onEach { delay(110) }
            .flatMapConcat { value ->
                if (value == 3) {
                    flow {
                        try {
                            delay(1000)
                            if (!sent4) {
                                emit(value)
                            }
                        } finally {
                            sent4 = false
                        }
                    }//.flowCancellable()
                } else {
                    sent4 = true
                    flowOf(value)
                }
            }
            .collect { value ->
                mylog("2: collect $value")
            }
    }

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

    private val myScope = CoroutineScope(Dispatchers.IO)
    private fun threadTest(use: Boolean) {
        val c = AtomicLong()
        for (i in 1..100000L) {
            if (use) {
                myScope.launch {
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

    fun mylog(msg: String) {
        println(LocalDateTime.now().toString() + ":" + msg)
    }
}