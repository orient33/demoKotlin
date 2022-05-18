package com.example

import java.io.File

fun mergeSame(defDimen: DimenFile, swDimen: DimenFile, mergeList: List<OneDimen>) {
    val mergeNameMap = mutableMapOf<String, String>()
    mergeList.forEach {
        mergeNameMap[it.key] = "${it.value}"
    }

    val defFile = File(defDimen.path)
    val defFile2 = File(defDimen.path + ".txt")

    var countMerge = 0
    var countNo = 0
    var otherLine = 0
    defFile.readLines().forEach {
        val p = readOneLine(it, false)
        if (p != null) {
            if (mergeNameMap.keys.contains(p.first)) {
                //count merge
                val newLine = it.replace("${p.second}", mergeNameMap[p.first]!!)
                defFile2.appendText("$newLine\n")
                ++countMerge
            } else {
                //count no merge
                ++countNo
                defFile2.appendText("$it\n")
            }
        } else {
            ++otherLine
            defFile2.appendText("$it\n")
        }
    }
    println("merge $countMerge, no $countNo, other $otherLine")
//    swDimen //del merge List. key

    if (mergeList.size == countMerge) {
        val sw2File = File(swDimen.path + ".txt")
        var countDel =0
        File(swDimen.path).readLines().forEach {
            val p = readOneLine(it, false)
            if (p != null && mergeNameMap.keys.contains(p.first)) {
                ++countDel
                println("del line ${p.first}")
            } else {
                sw2File.appendText(it + "\n")
            }
        }
        assert(countDel == countMerge)
    }
}


fun readOneLineWithUnit(line: String, log: Boolean = true): Pair<String, String>? {
    if (line.trim().startsWith("<dimen")) {
        val index = line.indexOf('"')
        val end = line.lastIndexOf('"')
        val name = line.substring(index + 1, end)

        val end2 = line.indexOf("</dimen>")
        val start2 = line.indexOf('>')
        val value = line.substring(start2 + 1, end2)
        return Pair(name, value)
    }
    return null
}