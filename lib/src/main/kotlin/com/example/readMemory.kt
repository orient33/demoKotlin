package com.example

import com.example.MyTest.mylog

const val ADB_PRE = "/home/dun/pro/sdk/platform-tools/"

// 通过adb检测android设备上某进程的内存
fun readMemory(packageName: String) {
//    mylog("env path is " + System.getenv("PATH"))
    //"which adb" 竟然找不到adb ,预计需要配置系统级别的环境变量.  ~/.bashrc下的不识别
    //1. 查找进程pid
    val pidResult = RunShell.runCmd("${ADB_PRE}adb shell ps -A|grep $packageName")
    val lines = pidResult.split("\n").filter { it.contains(packageName) }
    if (lines.isEmpty()) {
        mylog("not find process for $packageName")
        return
    }
    val words = lines[0].split(' ').filter { it.isNotEmpty() }
    //USER           PID  PPID     VSZ    RSS WCHAN            ADDR S NAME
    //theme        31253  1160 36236284 362136 do_epoll_wait      0 S com.android.thememanager
//  words[0/1/2]  uid , pid, ppid
    if (words.size < 4) return
    mylog("get .$words")

    val pid = words[1].toInt()
    // 2. dump pid 内存
    var count = 100//Short.MAX_VALUE
    var memInfo: MemInfo?
    do {
        memInfo = readMemForPid(pid)
        mylog("$memInfo")
        Thread.sleep(900)
    } while (count-- > 0)
}

fun readMemForPid(pid: Int): MemInfo? {
    val memResult = RunShell.runCmd("${ADB_PRE}adb shell dumpsys meminfo $pid")
    val timeline = System.currentTimeMillis() // 使用android设备的时间 ?
    if (memResult.length < 10) return null
    var nativeHeap = 0
    var javaHeap = 0
    var code = 0
    var stack = 0
    var graphics = 0
    var privateOther = 0
    var system = 0
    var totalPss = 0
//    val objects = mutableMapOf<String, Int>()
    memResult.split('\n').forEach {
        val line = it.trim()
        if (line.length > 1) {
            if (line.startsWith("Java Heap:")) {
                javaHeap = firstNumber(line)
            } else if (line.startsWith("Native Heap:")) {
                nativeHeap = firstNumber(line)
            } else if (line.startsWith("Code:")) {
                code = firstNumber(line)
            } else if (line.startsWith("Stack:")) {
                stack = firstNumber(line)
            } else if (line.startsWith("Graphics:")) {
                graphics = firstNumber(line)
            } else if (line.startsWith("Private Other:")) {
                privateOther = firstNumber(line)
            } else if (line.startsWith("System:")) {
                system = firstNumber(line)
            } else if (line.startsWith("TOTAL PSS:")) {
                totalPss = firstNumber(line)
            }
        }
    }
    return MemInfo(
        timeline,
        nativeHeap,
        javaHeap,
        code,
        stack,
        graphics,
        privateOther,
        system,
        totalPss,
    )
}

private fun firstNumber(line: String): Int {
    val words = line.split(' ').filter { it.isNotEmpty() && it.isNotBlank() && it.isAllNumber() }
    return if (words.isNotEmpty()) {
        words[0].toInt()
    } else {
        -1
    }
}

data class MemInfo(
    val timeline: Long,
    val nativeHeap: Int,
    val javaHeap: Int,
    val code: Int,
    val stack: Int,
    val graphics: Int,
    val privateOther: Int,
    val system: Int,
    val totalPss: Int,
//    val objects: Map<String, Int>,
)
/* ---内存的dumpsys 信息如下
dun@dun-OptiPlex-7050:~/下载$ adb shell dumpsys meminfo com.android.systemui
Applications Memory Usage (in Kilobytes):
Uptime: 128920828 Realtime: 756579279

** MEMINFO in pid 4485 [com.android.systemui] **
                   Pss  Private  Private  SwapPss      Rss     Heap     Heap     Heap
                 Total    Dirty    Clean    Dirty    Total     Size    Alloc     Free
                ------   ------   ------   ------   ------   ------   ------   ------
  Native Heap   110425   110412        0    28128   112764   262588   109367   146924
  Dalvik Heap    37967    37928        0     1169    41248    62934    38358    24576
 Dalvik Other    10438     8884        4      244    13616
        Stack     2412     2412        0      536     2420
       Ashmem      174      112        0        0     1752
      Gfx dev     7732     7464      268        0     7736
    Other dev       46        0       40        0      548
     .so mmap     4218      468       16      420    55420
    .jar mmap     2131        0       72        0    41780
    .apk mmap    25183        0    18020        0    41992
    .ttf mmap     3157        0      200        0    16432
    .dex mmap     5884     3692     2076     3768     6852
    .oat mmap      296        0        0        0    13572
    .art mmap     3241     3048        8       62    18320
   Other mmap      427        8      292        0     1584
   EGL mtrack     6592     6592        0        0     6592
    GL mtrack     1452     1452        0        0     1452
      Unknown      678      676        0      505     1240
        TOTAL   257285   183148    20996    34832   385320   325522   147725   171500

 App Summary
                       Pss(KB)                        Rss(KB)
                        ------                         ------
           Java Heap:    40984                          59568
         Native Heap:   110412                         112764
                Code:    24544                         179196
               Stack:     2412                           2420
            Graphics:    15776                          15780
       Private Other:    10016
              System:    53141
             Unknown:                                   15592

           TOTAL PSS:   257285            TOTAL RSS:   385320       TOTAL SWAP PSS:    34832

 Objects
               Views:     7857         ViewRootImpl:       26
         AppContexts:       94           Activities:        0
              Assets:       50        AssetManagers:        0
       Local Binders:      541        Proxy Binders:      155
       Parcel memory:      122         Parcel count:      198
    Death Recipients:       16      OpenSSL Sockets:        0
            WebViews:        0

 SQL
         MEMORY_USED:       89
  PAGECACHE_OVERFLOW:       29          MALLOC_SIZE:       46

 DATABASES
      pgsz     dbsz   Lookaside(b)          cache  Dbname
         4       20             27         1/22/4  :memory:
**/