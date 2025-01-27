package com.example.pip

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
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
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.StatusBarTool
import com.example.atLeast
import com.example.kotlindemo.IActivity
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentPipBinding
import com.example.log
import com.example.toast
import java.io.File
import java.util.*

class PiPFragment : Fragment(), AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {
    private val TAG: String = PiPFragment::class.java.simpleName

    private var width: Int = 1920
    private var height: Int = 720
    private var _binding: FragmentPipBinding? = null
    private val binding get() = _binding!!
    private var player: MediaCodecPlayer? = null
    private lateinit var vm: MediaVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vm = ViewModelProvider(this)[MediaVM::class.java]
        _binding = FragmentPipBinding.inflate(inflater)
        player = MediaCodecPlayer(_binding!!.surfaceView)
        lifecycle.addObserver(player!!)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarTool.adaptSysBar(view)
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
                ss.setSpan(
                    ForegroundColorSpan(Color.GREEN),
                    0,
                    vi.path.length,
                    SPAN_EXCLUSIVE_EXCLUSIVE
                )
                textView.append(ss)
                tv.tag = data[position]
                //:TODO 经常无访问权限 在10.0上
                val imageView = tv.findViewById<ImageView>(R.id.image)
                val bitmap = data[position].img
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher_background)
                }
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
        vm.getMediaData().observe(viewLifecycleOwner) { result ->
            data.clear()
            data.addAll(result)
            if (result.isEmpty()) {
                toast(requireActivity(), "未找到视频")
            } else {
                val a = binding.listView.adapter as BaseAdapter
                a.notifyDataSetChanged()
            }
        }
        findVideo(true, requireActivity())

        val dm = resources.displayMetrics
        width = dm.widthPixels
        height = dm.heightPixels
    }

    private fun enterPip(): Boolean {
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
        return true
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
        if (p != null) {
            p.play(tag.path)
            return
        }
        enterPip()
//        binding.videoView.setVideoURI(Uri.fromFile(File(tag.path)))
//        binding.videoView.visibility = View.VISIBLE
//        binding.videoView.start()
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        binding.surfaceView.visibility = View.VISIBLE
        val tag = view?.tag as VideoInfo
        (requireActivity() as IActivity).toFragment(PlayerFragment.newInstance(tag.path))
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findVideo(false, requireActivity())
            } else {
                toast(requireContext(), "无法读取存储")
            }
        }
    }

    @SuppressLint("SdCardPath", "StaticFieldLeak")
    private fun findVideo(checkPermission: Boolean, act: Activity) {
        if (checkPermission) {
            val permission =
                if (atLeast(33)) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
            if (ActivityCompat.checkSelfPermission(
                    act,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(permission), 1)
                return
            } else {
                log("already has permission : $permission")
            }
        }
        vm.loadVideo(act.application)
    }


    data class VideoInfo(val id: Long, val path: String, val img: Bitmap?, val title: String?)

    private val data = mutableListOf<VideoInfo>()
}
