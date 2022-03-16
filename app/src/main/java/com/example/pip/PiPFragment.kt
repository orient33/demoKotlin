package com.example.pip

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Rational
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.MediaController
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.kotlindemo.MainActivity
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentPipBinding
import com.example.log
import com.example.toast
import java.io.File
import java.util.*

class PiPFragment : Fragment(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private val TAG: String = PiPFragment::class.java.simpleName

    private var width: Int = 1920
    private var height: Int = 720
    private var _binding :FragmentPipBinding?=null
    private val binding get() = _binding!!
    private var player: MediaCodecPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPipBinding.inflate(inflater)
        player = MediaCodecPlayer(_binding!!.surfaceView)
        lifecycle.addObserver(player!!)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.videoView.setMediaController(MediaController(context))
        binding.listView.onItemClickListener = this
        binding.listView.onItemLongClickListener = this
        binding.listView.adapter = object : BaseAdapter() {
            @SuppressLint("SetTextI18n")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val tv = (convertView
                        ?: layoutInflater.inflate(R.layout.item_pip_fragment, parent, false))
                val vi = data[position]
                val textView = tv.findViewById<TextView>(android.R.id.text1)
                textView.text = "${vi.id} --${vi.title}  PATH: "
                val ss = SpannableString(vi.path)
                ss.setSpan(ForegroundColorSpan(Color.GREEN), 0, vi.path.length, SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.append(ss)
                tv.tag = data[position]
                //:TODO 经常无访问权限 在10.0上
//                val imageView = tv.findViewById<ImageView>(R.id.image)
//                if (TextUtils.isEmpty(vi.path)){
//                    if (TextUtils.isEmpty(vi.imgPath)){
//                        imageView.setImageResource(R.mipmap.ic_launcher_round)
//                    } else {
//                        Glide.with(view.context).load(vi.imgPath).into(imageView)
//                    }
//                } else {
//                    Glide.with(view.context).load(vi.path).into(imageView)
//                }
                return tv
            }

            override fun getItem(position: Int): VideoInfo {
                return data[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                return data.size
            }
        }
        findVideo(true, requireActivity())

        val dm = resources.displayMetrics
        width = dm.widthPixels
        height = dm.heightPixels
    }

    private fun enterPip(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            val surfaceShow = binding.surfaceView.visibility != View.VISIBLE
            if (binding.videoView.visibility != View.VISIBLE && !surfaceShow) {
                return false
            }
            val h = 300
            val w = (300 * 9f / 16).toInt()
            val rect = Rect(width - w, height - h, width, height)
            val b = PictureInPictureParams.Builder()
                    .setSourceRectHint(rect)
                    .setAspectRatio(Rational(16, 9))
                    .build()
            toast(requireContext(), "进入画中画")
            activity?.enterPictureInPictureMode(b)
            true
        } else {
            false
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
//        if (isInPictureInPictureMode) {
//            close.visibility = View.GONE
//        } else {
//            close.visibility = View.INVISIBLE
//        }
        Log.d(TAG, "onPictureInPictureModeChanged: $isInPictureInPictureMode")
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val tag = view?.tag as VideoInfo
        val p = player
//        if (p != null) {
////            val pf = PlayerFragment.newInstance(tag.path)
////            val ma = requireActivity() as MainActivity
////            ma.toFragment(pf)
//            p.play(tag.path)
//            return
//        }
        binding.videoView.setVideoURI(Uri.fromFile(File(tag.path)))
        binding.videoView.visibility = View.VISIBLE
        binding.videoView.start()
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        onItemClick(parent,view, position, id)
        enterPip()
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findVideo(false, requireActivity())
            } else {
                toast(requireContext(), "无法读取存储")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("SdCardPath", "StaticFieldLeak")
    private fun findVideo(checkPermission: Boolean, act: Activity) {
        if (checkPermission) {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ActivityCompat.checkSelfPermission(act, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 1)
                return
            } else {
                log("has permission : $permission")
            }
        }
//        val cr = activity?.contentResolver
//        if (cr == null) return
        val cr = activity?.contentResolver ?: return
        val task =
                object : AsyncTask<Void, Void, List<VideoInfo>>() {
                    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
                    override fun doInBackground(vararg params: Void?): List<VideoInfo> {
                        val result: MutableList<VideoInfo> = mutableListOf()
                        var cursor: Cursor? = null
                        try {
                            cursor = MediaStore.Video.query(cr,
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    null)
                            val idIndex = cursor.getColumnIndex(BaseColumns._ID)
                            val pathIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
                            val titleIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE)
                            if (cursor.moveToFirst()) {
                                var path: String
                                var title: String
                                var id: Long
                                do {
                                    id = cursor.getLong(idIndex)
                                    path = cursor.getString(pathIndex)
                                    title = cursor.getString(titleIndex)
                                    val vi = VideoInfo(id, path, "", title)
                                    result.add(vi)
                                } while (cursor.moveToNext())
                            }
                        } finally {
                            cursor?.close()
                        }
                        result.addAll(loadAudio(cr))
                        return result
                    }

                    override fun onPostExecute(result: List<VideoInfo>) {
                        data.clear()
                        data.addAll(result)
                        if (result.isEmpty()) {
                            toast(activity!!, "未找到视频")
                        } else {
                            val a = binding.listView.adapter as BaseAdapter
                            a.notifyDataSetChanged()
                        }
                    }
                }
        task.execute()
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun loadAudio(cr: ContentResolver): List<VideoInfo> {
        val result = ArrayList<VideoInfo>()
        val ex = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var cursor: Cursor? = null
        try {
            cursor = cr.query(ex, null, null, null, null, null)
            if (cursor == null) {
                Log.w("df", "cursor null. when query audio.")
                return result
            }

            val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val pathIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
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
                    val imgPath = getAudioImage(cr, albumId)
                    val vi = VideoInfo(id, path, imgPath, title)
                    result.add(vi)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
        return result
    }

    // fail..暂不能获取 音乐的图片;
    // 方法是ok的, 在 moto-g的手机上可以.
    // 但是在M01上,不能获取, 原因就是 找到的imagePath 不存在. 应该是ROM/系统的问题.
    private fun getAudioImage(cr: ContentResolver, albumId: Int): String {
        var cursor:Cursor? = null
        try {
            cursor = cr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Audio.Albums.ALBUM_ART),
                    MediaStore.Audio.Albums._ID + "=" + albumId, null, null) ?: return ""
            val imagePath: String // 类似 /storage/emulated/0/Android/data/com.android.providers.media/albumthumbs/1538130760010
            return if (cursor.moveToFirst()) {
                imagePath = cursor.getString(0)
                imagePath
            } else {
                ""
            }
        } catch (e: Exception) {
            return ""
        } finally {
            cursor?.close()
        }
    }

    data class VideoInfo(val id: Long, val path: String, val imgPath: String?, val title: String?)

    private val data = mutableListOf<VideoInfo>()
}
