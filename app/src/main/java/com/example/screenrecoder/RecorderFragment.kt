package com.example.screenrecoder

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.StatusBarTool
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentRecorderBinding

class RecorderFragment : Fragment(), View.OnClickListener, IRecorderCallback {

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            onMessageInfo("service disconnected!\n")
            mService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            onMessageInfo("service connected!\n")
            mService = (service as RecorderService.LocalBinder).getService()
            mService!!.setListener(this@RecorderFragment)
        }
    }
    private var mService: RecorderService? = null
    private var _binding: FragmentRecorderBinding? = null
    private val binding get() = _binding!!
    private var permissionLunch: ActivityResultLauncher<Array<String>>? = null
    private var mpLunch: ActivityResultLauncher<Intent>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        permissionLunch =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                onPermission(it)
            }
        mpLunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onPermissionScreenCapture(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecorderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        StatusBarTool.adaptSysBar(view)
        binding.start.setOnClickListener(this)
        binding.stop.setOnClickListener(this)
        val context = requireContext()
        context.bindService(
            Intent(context, RecorderService::class.java),
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onDestroyView() {
        if (mService != null) context?.unbindService(mServiceConnection)
        mService?.setListener(null)
        super.onDestroyView()
        _binding = null
    }

    override fun onMessageInfo(msg: String) {
        binding.text.text = msg
    }

    override fun onStateChange(recording: Boolean, mediaCodec: Boolean) {
        binding.start.isEnabled = !recording
        binding.stop.isEnabled = recording
        binding.spinner.setSelection(if (mediaCodec) 0 else 1)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.start -> startRecorder()
            R.id.stop -> {
                mService?.stopRecording()
            }
        }
    }

    private fun onPermission(result: Map<String, Boolean>) {
        var success = true
        result.forEach { (t, u) ->
            success = u && success
        }
        if (success) intentStartRecorder()
    }

    private fun startRecorder() {
        val _context = context ?: return
        val p = permissionLunch ?: return
        val has = ContextCompat.checkSelfPermission(
            _context,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (has) {
            intentStartRecorder()
        } else {
            p.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    private fun intentStartRecorder() {
        val pm =
            context?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = pm.createScreenCaptureIntent()
        // 申请录屏 将会拉起systemui相关dialog 等用户确认(选择单app 还是 整个屏幕)
        mpLunch?.launch(intent)
//        startActivityForResult(intent, RECORD_REQUEST_CODE)
    }

    private fun onPermissionScreenCapture(ar: ActivityResult) {
        mService?.startRecording(
            binding.spinner.selectedItemPosition == 0,
            ar.resultCode,
            ar.data!!
        )
    }
}