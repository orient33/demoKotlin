package com.example.a13

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.example.log

//https://developer.android.google.cn/about/versions/13/features/photopicker?hl=zh-cn
const val PHOTO_PICKER_REQUEST_CODE = 11
const val REQUEST_PHOTO_PICKER_MULTI_SELECT = 12

object PhotoPickerDemo {
    fun startPhotoPickerSingle(fragment:Fragment?, activity: Activity) {
        // Launches photo picker in single-select mode.
        // This means that the user can select one photo or video.
        val intent = if (Build.VERSION.SDK_INT >= 33) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            Intent(Intent.ACTION_PICK)
        }
//        intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 10)  //set max number
//        intent.type = "video/*"   //only video
//        intent.type = "image/*"   //only image
        if (fragment == null) {
            activity.startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE)
        } else {
            fragment.startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE)
        }
    }

    fun onPhotoResult(requestCode: Int, resultCode: Int, data: Intent?):Boolean {
        if (resultCode != Activity.RESULT_OK) return false
        when (requestCode) {
            PHOTO_PICKER_REQUEST_CODE -> {
                // Get photo picker response for single select.
                val currentUri: Uri = data?.data!!
                log("onPhotoResult. $currentUri")
                // Do stuff with the photo/video URI.
                return true
            }
            REQUEST_PHOTO_PICKER_MULTI_SELECT -> {
                // Get photo picker response for multi select.
                var i = 0
                while (i < data!!.clipData!!.itemCount) {
                    log("onPhotoResult2. ${data.clipData!!.getItemAt(i)}")
                }
                return true
            }
        }
        return false
    }
}