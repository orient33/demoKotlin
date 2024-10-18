package com.example.screenrecoder

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.StatusBarTool
import com.example.kotlindemo.R
import com.example.kotlindemo.databinding.FragmentRecorderBinding
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

const val PERMISSION_REQ = 1
const val RECORD_REQUEST_CODE = 10

@RequiresApi(21)
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
    private var _binding : FragmentRecorderBinding?=null
    private val binding get() = _binding!!
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            requireContext().startService(Intent(requireContext(), RecorderService::class.java))
            mService!!.startRecording(binding.spinner.selectedItemPosition == 0, resultCode, data)
        }
    }

    private fun startRecorder() {
        val _context = context ?: return
        val has = EasyPermissions.hasPermissions(
            _context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
        if (has) {
            intentStartRecorder()
        } else {
            EasyPermissions.requestPermissions(
                this, "录屏需要使用的权限", PERMISSION_REQ,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    @TargetApi(21)
    @AfterPermissionGranted(PERMISSION_REQ)
    private fun intentStartRecorder() {
        if (Build.VERSION.SDK_INT >= 21) {
            val pm =
                context?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val intent = pm.createScreenCaptureIntent()
            startActivityForResult(intent, RECORD_REQUEST_CODE)
        }
    }

}