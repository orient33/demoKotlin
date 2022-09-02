package com.example

fun String.isAllNumber(): Boolean {
    var isDigit = true
    this.forEach {
        isDigit = isDigit && it.isDigit()
    }
    return isDigit
}