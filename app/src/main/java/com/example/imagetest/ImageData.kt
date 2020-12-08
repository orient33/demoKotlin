package com.example.imagetest

data class ImageData(
    val name: String,       //图片标题
    val imageUrl: String,    //图片的url， http or filePath
    val aspRatio: Float = 16f / 9, //宽高比 h = w * ratio
    val des: String = ""
)