package com.example.kotlindemo

/**
 * @author dundongfang on 2018/2/5.
 */
data class UpdateResult(val pkgName: String?,
                        val file: String?,
                        val fullApk: String?,
                        val file_md5: String?) {
    fun isValid(): Boolean {
        return file != null && file.isNotEmpty()
    }
}