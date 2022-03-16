package com.example

import java.lang.StringBuilder
import java.nio.charset.Charset

object RunShell {
    fun runCmd(cmd: String, param: Array<String>? = null): String {
        val params = StringBuilder(" ")
        param?.forEach {
            params.append(it).append(' ')
        }

        val p = if (param == null || param.isEmpty()) {
            Runtime.getRuntime().exec(cmd)
        } else {
            Runtime.getRuntime().exec("$cmd $params")
        }
//        val error = p.errorStream.reader(Charsets.UTF_8).readText()
        val out = p.inputStream.reader(Charset.defaultCharset()).readText()
//        println("-------------- run  $cmd  $param ,result : -------------")
//        println("out: $out")
//        if (error.isNotEmpty()) {
//            println("error: $error")
//        }
        return out
    }

}