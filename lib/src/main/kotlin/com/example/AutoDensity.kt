package com.example

import java.io.File
import kotlin.math.abs

const val THEME_ROOT = "/home/dun/work/ThemeManager"

val logFile = File("$THEME_ROOT/readme.txt")

data class OneDimen(val key: String, val value: Float)

data class DimenFile(
    val key: List<String>,
    val path: String,
    val dimenValue: MutableList<OneDimen> = mutableListOf()
)

val noDefMap = mutableMapOf<String, String>()
val defFiles = mutableListOf<DimenFile>()

fun autoDensity(dir: String = "") {
    ///home/dun/work/ThemeManager/basemodule  module_detail module_recommend module_mine app
    if (dir.isEmpty()) {
        log("begin-----------", false)
        val dirList =
            listOf("basemodule", "module_detail", "module_recommend", "module_mine", "app")
        dirList.forEach {
            autoDensity("$THEME_ROOT/$it")
        }
        log("end-----------")
        checkDefault(defFiles, noDefMap)
        return
    }
    val dimenFileList = findDimenFileList(dir).filter {
        isPhoneDimen(it.path)
    }

    var defaultDimen: DimenFile? = null
    dimenFileList.forEach {
        val dimens = readDimenFile(it)
        it.dimenValue.addAll(dimens)
        log("${it.path}, keys size = ${dimens.size}")
        if (it.key.isEmpty() || (it.key.size == 1 && it.key.isEmpty())) {
            defaultDimen = it
            log("find default. ${it.path}")
            defFiles.add(it)
        }
    }
    if (defaultDimen == null) throw IllegalStateException("not find default dimen, for path $dir")

    //
    val default = defaultDimen!!
    dimenFileList.forEach {
        if (it != default) {
            val scale = findScaleToDefault(it.key)

            var countSame = 0
            var count1 = 0
            var count2 = 0
            var countMuch = 0

            val sameDimenList = mutableListOf<OneDimen>()
            it.dimenValue.forEach { dimen ->
                val def = findSameDimen(default, dimen.key)
                if (def == null) { //not find default value.
                    noDefMap[dimen.key] = it.path
                    log("--not find default value $dimen")
                } else {
                    val diff = abs(def.value * scale - dimen.value)
                    val pxdiff = diff * 2.75f
                    if (pxdiff < 1) {
                        ++countSame
                        sameDimenList.add(dimen)
                    } else if (pxdiff < 1.5) {
                        ++count1
                    } else if (pxdiff < 2.5) {
                        ++count2
                    } else {
                        ++countMuch
                        log("diff too much! $diff == ${def.key},${def.value} ${dimen.value}")
                    }
                }
            }

            log("diff default with ${it.key}, same=$countSame, count1=$count1, count2=$count2, much=$countMuch")
            mergeSame(default, it,sameDimenList)
        }
    }
}

fun checkDefault(list: List<DimenFile>, map: Map<String, String>) {

    println("no default size : ${map.size}")

    var findCount = 0
    list.forEach { df ->
        df.dimenValue.forEach {
            if (map.keys.contains(it.key)) {
                println("find default, ${it.key} ,in ${df.path} ${map[it.key]}")
                ++findCount
            }
        }
    }
    println("find size. $findCount")
}

//read dimens.xml from folder/dir
fun findDimenFileList(dir: String): List<DimenFile> {
    val dimenList = RunShell.runCmd("find ", arrayOf(dir, " -name ", "dimen*.xml"))
    val list = dimenList.split("\n").filter { it.endsWith("xml") }
    // /home/...module/src/main/res/values/dimens.xml
    val fileList = mutableListOf<DimenFile>()
    list.forEach {
        val last = it.lastIndexOf('/')
        val sub = it.substring(0, last)
        val last2 = sub.lastIndexOf('/')
        val values = it.substring(last2, last).split('-').filter { it2 -> !it2.startsWith('/') }

        fileList.add(DimenFile(values, it))
    }
    return fileList
}

fun isPhoneDimen(path: String): Boolean {
    if (path.contains("/pad/") || path.contains("/build/")) return false
    val indexSw = path.indexOf("sw")
    if (indexSw < 0) return true
    val swXdp = path.substring(indexSw + 2, indexSw + 5)
    val arrayPhone = arrayListOf<Int>(392, 411)
    return arrayPhone.contains(swXdp.toInt())
}

fun readDimenFile(file: DimenFile): List<OneDimen> {
    val dimenList = mutableListOf<OneDimen>()
    File(file.path).readLines().forEach { line ->
        val p = readOneLine(line)
        if (p != null) {
            dimenList.add(OneDimen(p.first, p.second))
        }
    }
    dimenList.sortBy { it.key }
    return dimenList
}

fun readOneLine(line: String, log: Boolean = true): Pair<String, Float>? {
    if (line.trim().startsWith("<dimen")) {
        val index = line.indexOf('"')
        val end = line.lastIndexOf('"')
        val name = line.substring(index + 1, end)

        val end2 = line.indexOf("</dimen>")
        val start2 = line.indexOf('>')
        val value = line.substring(start2 + 1, end2)
        return if (value.endsWith("dp") || value.endsWith("sp")) {
            Pair(name, value.substring(0, value.length - 2).toFloat())
        } else if (value.endsWith("dip")) {
            Pair(name, value.substring(0, value.length - 3).toFloat())
        } else if (value.endsWith("px")) {
            if (log) log("px value. $name, $value")
            null
        } else {
            if (log) log("unknown unit $name $value")
            null
//            throw IllegalStateException("readOneLine. value error: $value, for $name")
        }
    }
    return null
}

fun findScaleToDefault(keys: List<String>): Float {
    var scale = 1f
    keys.forEach {
        scale = if (it.startsWith("sw")) {
//            "sw392dp" -> 1.08f *scale
//            "sw411dp" -> 411f/360 *scale
            val sw = it.substring(2, 5).toFloat()
            log("scale for $sw == ${sw / 360f}")
            scale * (sw / 360)
        } else if (it.endsWith("hdpi")) {
            scale
        } else {
            scale
        }

    }
    return scale
}

fun findSameDimen(dimenFile: DimenFile, name: String): OneDimen? {
    var dimen: OneDimen? = null
    dimenFile.dimenValue.forEach {
        if (it.key == name) dimen = it
    }
    return dimen
}

fun log(msg: String, append: Boolean = true) {
    val noAbsPath = msg.replace(THEME_ROOT, "~")
//    println(noAbsPath)
    if (append) {
        logFile.appendText(noAbsPath + "\n")
    } else {
        logFile.writeText(noAbsPath + "\n")
    }
}
