package com.example

import java.io.File

class OpMoveDimen(
    val attr: String,
    val fileA: File,
    val fileB: File,
)

fun doMoveDimen(op: OpMoveDimen) {
    var lineA = ""
    for (line in op.fileA.readLines()) {
        if (line.contains("\"${op.attr}\"")) {
            lineA = line
            break
        }
    }
    if (lineA.isEmpty()) return/// throw IllegalStateException("not find ${op.attr} in ${op.fileA}")

    val content = StringBuilder()
    var moveCount = 0
    op.fileB.readLines().forEach {
        if (it.contains("\"${op.attr}\"")) {
            content.append(lineA).append('\n')
            moveCount++
        } else {
            content.append(it).append('\n')
        }
    }
    assert(moveCount == 1) {
        "move count not 1?"
    }
    op.fileB.writeText(content.toString())
}

fun doDeleteDimen(file: File, attrsList: List<String>) {
    val content = StringBuilder()
    var filterCount = 0
    file.readLines().forEach {
        if (it.contains("color")) {
            val attr = it.substring(it.indexOf('"') + 1, it.indexOf('>') -1)
//                it.substring(it.indexOf(">") + 1, it.lastIndexOf("<"))
            if (!attrsList.contains(attr)) {
                content.append(it).append('\n')
            } else {
                filterCount++
            }
        } else {
            content.append(it).append('\n')
        }
    }
    assert(attrsList.size == filterCount) {
        "delete fail.. list ${attrsList.size}, filter $filterCount"
    }
    file.writeText(content.toString())
}


