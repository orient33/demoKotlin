package com.example.screenrecoder

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlindemo.R
import kotlinx.android.synthetic.main.fragment_recorder.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

const val PERMISSION_REQ = 1
const val RECORD_REQUEST_CODE = 10

class RecorderFragment : Fragment(), View.OnClickListener {
    var recorderTool: RecorderTool? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recorder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        start.setOnClickListener(this)
        stop.setOnClickListener(this)
    }

    override fun onDestroyView() {
        recorderTool?.stop()
        super.onDestroyView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.start -> startRecorder()
            R.id.stop -> stopRecorder()
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
        if (Build.VERSION.SDK_INT < 21) return
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val pm =
                context?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val mp = pm.getMediaProjection(resultCode, data)
            val localContext = context ?: return
            recorderTool = RecorderTool(localContext.applicationContext, mp)
            recorderTool?.startVideo()
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

    private fun stopRecorder() {
        recorderTool?.stop()
    }
}