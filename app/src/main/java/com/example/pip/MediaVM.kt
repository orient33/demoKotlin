package com.example.pip

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.atLeast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class MediaVM(val app: Application) : AndroidViewModel(app) {
    private val videoData = MutableLiveData<List<PiPFragment.VideoInfo>>()

    fun getMediaData(): LiveData<List<PiPFragment.VideoInfo>> = videoData

    fun loadVideo(ctx: Context) {
        val old = videoData.value
        if (old == null || old.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val all = loadVideoInternal()
                loadAppVideo(ctx, all)
                videoData.postValue(all)
            }
        }
    }

    private fun loadAppVideo(ctx: Context, list: MutableList<PiPFragment.VideoInfo>) {
        val d = ctx.getExternalFilesDir(null) ?: return
        d.listFiles { _, name ->
            name != null && name.endsWith(".mp4")
        }?.forEach {
            list.add(PiPFragment.VideoInfo(-1, it.path, null, it.name))
        }
    }

    private fun loadVideoInternal(): MutableList<PiPFragment.VideoInfo> {
        val cr = app.contentResolver
        val result: MutableList<PiPFragment.VideoInfo> = mutableListOf()
        cr.query(
            if (atLeast(Build.VERSION_CODES.Q)) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            },
            null, null, null, null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(BaseColumns._ID)
            val pathIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
            val titleIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE)
            val displayNameIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                var path: String
                var title: String
                var id: Long
                do {
                    id = cursor.getLong(idIndex)
                    path = cursor.getString(pathIndex)
                    title = cursor.getString(titleIndex)
                    val img = if (atLeast(30)) {
                        try {
                            cr.loadThumbnail(
                                MediaStore.Video.Media.getContentUri(
                                    MediaStore.VOLUME_EXTERNAL,
                                    id
                                ),
                                Size(100, 100), null
                            )
                        } catch (e: Exception) {
                            Log.w("df", "load thumb fail . $e")
                            null
                        }
                    } else null
                    val dn = cursor.getString(displayNameIndex)
//                    if (title.isNullOrEmpty()) title = dn
                    Log.i("df", "loadVideo: $id,$title,$dn")
                    val vi = PiPFragment.VideoInfo(id, path, img, title)
                    result.add(vi)
                } while (cursor.moveToNext())
            }
        }
        result.addAll(loadAudio(cr))
        return result
    }


    private fun loadAudio(cr: ContentResolver): List<PiPFragment.VideoInfo> {
        val result = ArrayList<PiPFragment.VideoInfo>()
        val ex = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        cr.query(ex, null, MediaStore.Audio.Media.DURATION + " > 15", null, null, null)
            .use { cursor ->
                if (cursor == null || cursor.count < 1) {
                    Log.w("df", "cursor null or empty. when query audio.")
                    return result
                }

                val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val pathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val di = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                if (cursor.moveToFirst()) {
                    var path: String
                    var title: String
                    var id: Long
                    var albumId: Int
                    do {
                        id = cursor.getLong(idIndex)
                        path = cursor.getString(pathIndex)
                        title = cursor.getString(titleIndex)
                        albumId = cursor.getInt(albumIdIndex)
                        val disName = cursor.getString(di)
                        val img = getAudioImage(cr, id, albumId)
                        Log.i("df", "loadAudio: $id,$title,$albumId,$disName")
                        val vi =
                            PiPFragment.VideoInfo(id, path, if (img is Bitmap) img else null, title)
                        result.add(vi)
                    } while (cursor.moveToNext())
                }
            }
        return result
    }

    // fail..暂不能获取 音乐的图片;
    // 方法是ok的, 在 moto-g的手机上可以.
    // 但是在M01上,不能获取, 原因就是 找到的imagePath 不存在. 应该是ROM/系统的问题.
    //多
    private fun getAudioImage(cr: ContentResolver, id: Long, albumId: Int): Any? {
        if (atLeast(30)) {
            return try {
                cr.loadThumbnail(
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL, id),
                    Size(100, 100),
                    null
                )
            } catch (e: Exception) {
                null
            }
        } else {
            cr.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Albums.ALBUM_ART),
                MediaStore.Audio.Albums._ID + "=" + albumId, null, null
            )?.use { cursor ->
                val imagePath: String // 类似 /storage/emulated/0/Android/data/com.android.providers.media/albumthumbs/1538130760010
                return if (cursor.moveToFirst()) {
                    imagePath = cursor.getString(0)
                    imagePath
                } else {
                    ""
                }
            }

        }
        return ""
    }

}