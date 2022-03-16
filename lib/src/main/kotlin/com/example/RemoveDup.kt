package com.example

import java.io.File

object RemoveDup {
    fun removeDupColor() {
        val logFile = File("/home/dun/code.open/demoKotlin/lib/theme2.txt")
        var attrSize = 0
        var diffDimenCount = 0
        val otherKey = mutableMapOf<String, Int>()

        val deleteMap = mutableMapOf<String, MutableList<String>>()
        val moveList = mutableListOf<OpMoveDimen>()
        logFile.readLines().forEach { s ->
            if (s.startsWith('[')) {
                val key = s.substring(1, firstNonAZ(s))
                if (key == "color") {
                    val sep = s.subSequence(0, s.indexOf(':'))
                    val ss = sep.split(" ")
                    if (ss.size == 4) {
                        val fileA = File(ss[1])
                        val fileB = File(ss[3])
                        if (fileA.exists() && fileB.exists()) {
                            val attr = ss[0].substring(ss[0].lastIndexOf('/') + 1, ss[0].length - 1)
                            val vA = readAttrFromXml(attr, fileA)
                            val vB = readAttrFromXml(attr, fileB)
                            if (vA != vB) {
                                println("diff color $attr $vA  $vB")
                                diffDimenCount++
                                moveList.add(OpMoveDimen(attr, fileA, fileB))
                            } else {
                                val v = deleteMap[fileA.path]
                                if (v == null) {
                                    val list = mutableListOf<String>()
                                    deleteMap[fileA.path] = list
                                    list.add(attr)
                                } else {
                                    v.add(attr)
                                }
                            }
                            attrSize++
                        } else {
                            println("file not exist.? $fileA $fileB")
                        }
                    }
                } else {
                    if (!otherKey.contains(key)) {
//                        println("add key $key")
                        otherKey[key] = 1
                    } else {
                        val v = otherKey[key]
                        otherKey[key] = v!! + 1
                    }
                }
            }
        }

        deleteMap.forEach { (path, mutableList) ->
            println("delete xml $path, size = ${mutableList.size}")
            doDeleteDimen(File(path), mutableList)
        }
//
//        moveList.forEach {
//            doMoveDimen(it)
//        }

        println("color size = $attrSize , diff count = $diffDimenCount\n,")

        otherKey.forEach { (s, i) ->
            println(" $s : $i")
        }
    }


    fun removeDupDimen() {
        val logFile = File("/home/dun/code.open/demoKotlin/lib/theme2.txt")
        var attrSize = 0
        var diffDimenCount = 0
        val otherKey = mutableMapOf<String, Int>()

        val deleteMap = mutableMapOf<String, MutableList<String>>()
        val moveList = mutableListOf<OpMoveDimen>()
        logFile.readLines().forEach { s ->
            if (s.startsWith('[')) {
                val key = s.substring(1, firstNonAZ(s))
                if (key == "dimen") {
                    val sep = s.subSequence(0, s.indexOf(':'))
                    val ss = sep.split(" ")
                    if (ss.size == 4) {
                        val fileA = File(ss[1])
                        val fileB = File(ss[3])
                        if (fileA.exists() && fileB.exists()) {
                            val attr = ss[0].substring(ss[0].lastIndexOf('/') + 1, ss[0].length - 1)
                            val vA = readAttrFromXml(attr, fileA)
                            val vB = readAttrFromXml(attr, fileB)
                            if (vA != vB) {
                                println("diff $attr $vA  $vB")
                                diffDimenCount++
                                moveList.add(OpMoveDimen(attr, fileA, fileB))
                            } else {
                                val v = deleteMap[fileA.path]
                                if (v == null) {
                                    val list = mutableListOf<String>()
                                    deleteMap[fileA.path] = list
                                    list.add(attr)
                                } else {
                                    v.add(attr)
                                }
                            }
                            attrSize++
                        } else {
                            println("file not exist.? $fileA $fileB")
                        }
                    }
                } else {
                    if (!otherKey.contains(key)) {
//                        println("add key $key")
                        otherKey[key] = 1
                    } else {
                        val v = otherKey[key]
                        otherKey[key] = v!! + 1
                    }
                }
            }
        }

        deleteMap.forEach { (path, mutableList) ->
            println("delete xml $path, size = ${mutableList.size}")
            doDeleteDimen(File(path), mutableList)
        }

        moveList.forEach {
            doMoveDimen(it)
        }

        println("dimen size = $attrSize , diff count = $diffDimenCount\n,")

        otherKey.forEach { (s, i) ->
            println(" $s : $i")
        }
    }


    ///home/dun/code.mi/ThemeManager/module_recommend/src/main/res/drawable-xxhdpi/font_star_gray.png
    val file = File("/home/dun/code.open/demoKotlin/lib/theme2.txt")


    fun renamePngWebp() {
        val list = mutableListOf<String>()
        var tmpFile: File? = null
        file.readLines().forEach {
            var itTrim: String = it.trim()
            if (itTrim.startsWith("/")) {
                val i = itTrim.indexOf(":")
                if (i > 0) {
                    itTrim = itTrim.substring(0, i)
                }
                tmpFile = File(itTrim)
                if (tmpFile!!.exists()) {
                    list.add(tmpFile!!.path)
                }
            }
        }

        var count = 0
        list.forEach {
            val file = File(it)
            if (file.exists() && it.endsWith(".png")) {
                val name = it.substring(0, it.length - 3) + "webp"
                file.renameTo(File(name))
                count++
            }
        }
        println("rename count $count")
    }

    fun readAttrFromXml(attr: String, file: File): String {
        var count = 0
        var value = ""
        file.readLines().forEach {
            if (it.contains("\"$attr\"")) {
                val start = it.indexOf(">") + 1
                val end = it.lastIndexOf("<")
                value = it.substring(start, end)
                count++
                if (count > 1) {
                    throw IllegalArgumentException(" $attr has $count .count!")
                }
            }
        }

        return value
    }


    fun removeDupDrawable() {
        val logFile = File("/home/dun/code.open/demoKotlin/lib/theme2.txt")
        var removeCount = 0
        var diffCount = 0
        var sizeDiff = 0
        logFile.readLines().forEachIndexed { index, s ->
            if (s.startsWith('[')) {
                val key = s.substring(1, firstNonAZ(s))
                if (key == "drawable") {
                    val sep = s.subSequence(0, s.indexOf(':'))
                    val ss = sep.split(" ")
                    if (ss.size == 4) {
                        val fileA = File(ss[1])
                        val fileB = File(ss[3])
                        if (fileA.exists() && fileB.exists()) {
                            val nameNoSuf = fileA.name.substring(0, fileA.name.indexOf('.'))
                            if (sameSizeAppModule(nameNoSuf)) {
                                if (sameFile(fileA, fileB)) {
                                    val r = fileA.delete()
                                    if (!r) {
                                        println("delete A fail. ? $fileA")
                                    }
                                    removeCount++
                                } else {
                                    val r = fileA.copyTo(fileB, true)
                                    if (r != fileB) {
                                        println("md5 not same  copyf fail. $fileA $fileB")
                                    } else {
                                        val result = fileA.delete()
                                        if (!result) {
                                            println("delete A Fail--- $fileA")
                                        }
                                    }
                                    diffCount++
                                }
                            } else {
                                sizeDiff++
                                val r = fileA.copyTo(fileB, true)
                                if (r != fileB) {
                                    println("size not same  copy fail.... $fileA $fileB")
                                } else {
                                    val result = fileA.delete()
                                    if (!result) {
                                        println("delete A Fail22--- $fileA")
                                    }
                                }
                            }
                        } else {
                            throw IllegalArgumentException("fileA,or B not exist. $fileA $fileB")
                        }
                    } else {
                        throw IllegalArgumentException(" $sep, $ss")
                    }
                }
            } else {
//                throw IllegalArgumentException("line $index , value : $s ")
            }
        }
        println("remove File count $removeCount, diff File count $diffCount, sizeDiff $sizeDiff")
        rrr.forEach {
            println(it)
        }
    }

    private val dir = "/home/dun/code.mi/ThemeManager"
    private val len = dir.length + 1
    private val rrr = mutableSetOf<String>()

    //app和module是否相同个数的file
    private fun sameSizeAppModule(name: String): Boolean {
        val r = RunShell.runCmd("find ", arrayOf(dir, " -name ", "$name.*"))
        val files = r.split("\n").filter {
            it.isNotEmpty() && it.length > len
        }
        if (files.size % 2 != 0) {
            rrr.add(name)
            return false
        } else {
            var (dir1, size1) = Pair("", 0)
            var (dir2, size2) = Pair("", 0)
            for (file in files) {
                if (file.isEmpty()) {
                    continue
                }
                val sub = file.substring(len, file.length)
                val key = sub.substring(0, sub.indexOf('/'))
                if (dir1 == "" || dir1 == key) {
                    dir1 = key
                    size1++
                } else if (dir2 == "" || dir2 == key) {
                    dir2 = key
                    size2++
                } else {
                    if (dir1 == key) {
                        size1++
                    } else if (dir2 == key) {
                        size2++
                    } else {
                        throw IllegalArgumentException(" 3 module? $name")
                    }
                }
            }
            return size1 == size2
        }
//        mylog("out $r")
    }

    private fun sameFile(fileA: File, fileB: File): Boolean {
        val md5A = Md5Util.md5(fileA.readText())
        val md5B = Md5Util.md5(fileB.readText())
        return md5A.length > 1 && md5A == md5B
    }

    internal fun firstNonAZ(value: String): Int {
        var index = 0
        for (c in value) {
            if (c != '[' && !c.isLetter()) {
                return index
            }
            index++
        }
        return -1
    }

}