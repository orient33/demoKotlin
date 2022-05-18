package com.example

import java.io.File
import kotlin.math.abs

data class Dimen(
    val key: String,  //如 demo_margin
    val value: String, // 10dp 20sp 22dip ...
    val path: String,  // ~/Theme/app/src/...values-sw392/dimens.xml
    val fileKeys: List<String>, // sw392, land, night, xxhdpi
)

data class MergedDimen(
    val key: String,
    val list: MutableList<Dimen>
)

data class Op(
    val path: String, //file path
    val delete: Dimen?,//eed delete this dimen/key
    val edit: Dimen?, //need change edit.key to edit.value  (origin.value != edit.value)
)

data class OpDimen(
    val key: String,
    val list: List<Op>,
)

data class OpFile(
    val path: String,
    val deleteList: MutableList<Dimen>,
    val editList: MutableList<Dimen>,
)

val undoKey = arrayOf("land", "night")
val sw392 = "sw392dp"
val sw411 = "sw411dp"

fun autoDensity2() {
    val allPhoneDimen = findDimenFileList(THEME_ROOT).filter { isPhoneDimen(it.path) }

    val dimenList = mutableListOf<Dimen>()
    allPhoneDimen.forEach {
        val dimen1file = readDimenFile2(it)
        dimenList.addAll(dimen1file)
    }

    val mergedDimens = mutableMapOf<String, MergedDimen>()
    dimenList.forEach {
        val old = mergedDimens[it.key]
        if (old == null) {
            mergedDimens[it.key] = MergedDimen(it.key, mutableListOf(it))
        } else {
            old.list.add(it)
        }
    }

    val opDimenList = mutableListOf<OpDimen>()
    mergedDimens.values.forEach {
        val op = judgeDimen(it)
        if (op != null) {
            opDimenList.add(op)
        }
    }

    val opFileList = mutableMapOf<String, OpFile>()
    opDimenList.forEach { opDimen ->
        opDimen.list.forEach {
            val old = opFileList[it.path]
            if (old == null) {
                val deleteList = mutableListOf<Dimen>()
                if (it.delete != null) {
                    deleteList.add(it.delete)
                }
                val editList = mutableListOf<Dimen>()
                if (it.edit != null) {
                    editList.add(it.edit)
                }
                opFileList[it.path] = OpFile(it.path, deleteList, editList)
            } else {
                if (it.delete != null) old.deleteList.add(it.delete)
                if (it.edit != null) old.editList.add(it.edit)
            }
        }
    }

    opFileList.forEach { (path, opFile) ->
        println("$path, need delete ${opFile.deleteList.size}, edit ${opFile.editList.size}")
        val writePath = File("$path.txt")
        writePath.writeText("")
        val readFile = File(path)
        readFile.readLines().forEach {
            val p = readOneLineWithUnit(it)
            if (p != null) {
                val index = indexOfKey(opFile.deleteList, p.first)
                val eIndex = indexOfKey(opFile.editList, p.first)
                if (index >= 0) {
                    //do not write
                } else if (eIndex >= 0) {
                    //edit to new dimen value
                    val newDimen = opFile.editList[eIndex]
                    val newLine = it.replace(p.second, newDimen.value)
                    writePath.appendText(newLine + "\n")
                } else {
                    //same as read
                    writePath.appendText(it + "\n")
                }
            } else {
                if (it.contains("</resources>")) {
                    writePath.appendText(it)
                } else {
                    writePath.appendText("$it\n")
                }
            }
        }
        //
        readFile.delete()
        writePath.renameTo(readFile)
    }
}

fun readDimenFile2(file: DimenFile): List<Dimen> {
    val dimens = mutableListOf<Dimen>()
    File(file.path).readLines().forEach { line ->
        val p = readOneLineWithUnit(line)
        if (p != null) {
            dimens.add(Dimen(p.first, p.second, file.path, file.key))
        }
    }
//    dimens.sortBy { it.key }
    return dimens
}

fun judgeDimen(md: MergedDimen): OpDimen? {
    val def =
        md.list.filter { it.fileKeys.isEmpty() || (it.fileKeys.size == 1 && it.fileKeys[0].isEmpty()) }
    if (def.size>2){
        throw IllegalStateException("has much default.${def.size} $md")
    } else if (def.size > 1) {
        println(" much default.${def.size} $md")
        if (def[0].value == def[1].value) {
        } else {
            throw IllegalStateException("much default.not same $md")
        }
    } else if (def.size == 1) {
        val opList = mutableListOf<Op>()
        val defDimen = def[0]
        var dimen392: Dimen? = null
        var dimen411: Dimen? = null
        var sameWith392 = false
        var sameWith411 = false
        md.list.forEach {
            if (it != defDimen) {
                val sw = findSwKey(it.fileKeys)
                if (!isUnitSame(it, defDimen)) {
//                    throw
                    println("unit not same. $defDimen, $it")
                } else {
                    if (sw == sw392) {
                        dimen392 = it
                        sameWith392 = isValueSame(defDimen, it, 392)
                    } else if (sw == sw411) {
                        dimen411 = it
                        sameWith411 = isValueSame(defDimen, it, 411)
                    } else if (sw.isNotEmpty()){
                        println("other sw. $sw")
                    }
                }
            }
        }

        if (sameWith392 && sameWith411) {
            opList.add(Op(dimen411!!.path, dimen411, null)) // delete sw411
            opList.add(Op(dimen392!!.path, dimen392, null)) // delete sw392
            opList.add(Op(defDimen.path, null, dimen392)) // edit default to sw392
        } else if (sameWith392) {
            opList.add(Op(dimen392!!.path, dimen392, null)) // delete sw392
            opList.add(Op(defDimen.path, null, dimen392)) // edit default to sw392
        } else if (sameWith411) {
            println("sw392 not same. but sw411 same for default...$defDimen, $dimen392, $dimen411")
        }
        return OpDimen(md.key, opList)
    } else {
        // has no default..do nothing
    }
    return null
}

fun indexOfKey(dimens: List<Dimen>, key: String): Int {
    dimens.forEachIndexed { index, dimen ->
        if (dimen.key == key) {
            return index
        }
    }
    return -1
}

fun isValueSame(defDimen: Dimen, it: Dimen, sw: Int): Boolean {
    val a = defDimen.value.filter { c -> c.isDigit() || c == '.' }.toFloat()
    val b = it.value.filter { c -> c.isDigit() || c == '.' }.toFloat()
    val diffPx = abs((a - b) * (sw / 360f))
    return diffPx < 1.5
}

fun isUnitSame(def: Dimen, b: Dimen): Boolean {
    val unit1 = def.value.filter { it.isLetter() }
    val unit2 = b.value.filter { it.isLetter() }
    if (unit1.isDp() && unit2.isDp()) return true
    if (unit1.isSp() && unit2.isSp()) return true
    if (unit1.isPx() && unit2.isPx()) return true
    println("unit not same. $unit1, $unit2,,, $def, $b")
    return false
}

fun findSwKey(keys: List<String>): String {
    val sw = keys.filter { it.startsWith("sw") }
    assert(sw.size <= 1)
    return if (sw.isEmpty()) {
        ""
    } else {
        sw[0]
    }
}

fun String.isDp(): Boolean {
    return this == "dp" || this == "dip"
}

fun String.isSp(): Boolean {
    return this == "sp"
}

fun String.isPx(): Boolean {
    return this == "px"
}