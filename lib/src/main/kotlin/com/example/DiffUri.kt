package com.example


fun compareUrl(u1: String, u2: String) {
    val p1 = u1.substring(u1.indexOf('?') + 1)
    val p2 = u2.substring(u2.indexOf('?'))
    val map1 = genMap(p1)
    val map2 = genMap(p2)
    println("map1 size ${map1.size}, map2.size ${map2.size}")
    val diff1 = mutableMapOf<String, String?>()
    map1.forEach {
        if (!map2.containsKey(it.key)) {
            diff1[it.key] = it.value
        }
    }
    val diff2 = mutableMapOf<String, String?>()
    map2.forEach {
        if (!map1.containsKey(it.key)) {
            diff2[it.key] = it.value
        }
    }
    if (diff1.isNotEmpty()) {
        diff1.forEach { (k, v) ->
            println("1 has, $k, $v,  2 not contain !")
        }
    }
    if (diff2.isNotEmpty()) {
        diff2.forEach { (k, v) ->
            println("2 has $k, $v, 1 not contain!")
        }
    }
}

fun genMap(params: String): Map<String, String?> {
    val map = mutableMapOf<String, String?>()
    params.split('&').forEach {
        val removed = it.replace("?","")
        val kv = removed.split('=')
        if (kv.size == 2 && kv[1].isNotEmpty()) {
            map[kv[0]] = kv[1]
        }
    }
    return map
}