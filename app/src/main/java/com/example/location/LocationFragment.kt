package com.example.location


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.example.kotlindemo.databinding.FragmentLocationBinding
import com.example.log

/**
 * 定位fragment示例. LocationManager
 *
 */
class LocationFragment : Fragment(), OnClickListener, View.OnLongClickListener {
    private lateinit var lm: LocationManager
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lm = view.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        binding.getLocation.setOnClickListener(this)
        binding.getLocation.setOnLongClickListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("df", "onRequestPermissionResult. $requestCode , $permissions,$grantResults")
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            reqLocation(false)
        }
    }

    override fun onLongClick(v: View): Boolean {
        reqLocation(true)
        return true
    }

    override fun onClick(v: View) {
        val c = context ?: return
        val act = activity ?: return
        if (ActivityCompat.checkSelfPermission(
                c,
                permissions[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            log("has no location permission.")
            if (ActivityCompat.shouldShowRequestPermissionRationale(act, permissions[0])) {
                Log.d("df", "应当解释为何需要权限!")
//                ActivityCompat.requestPermissions(act, permissions, 1)
                requestPermissions(permissions, 1)
            } else {
                Log.d("df", "请求权限")
//                ActivityCompat.requestPermissions(act, permissions, 1)
                requestPermissions(permissions, 1)
            }
        } else {
            log("has permission")
            reqLocation(false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun reqLocation(gps: Boolean) {
        binding.locationInfo.text = ""
        val cr = Criteria()
        cr.powerRequirement = Criteria.POWER_LOW
        lm.allProviders.forEach {
            binding.locationInfo.append("$it , ")
        }
        val it = if (gps) "gps" else lm.getBestProvider(cr, true)
        log("provider : $it")
        binding.locationInfo.append("\n ------ provider: $it ---------\n")
        val location = lm.getLastKnownLocation(it)
        binding.locationInfo.append("最后位置: ${location2String(location)}\n")
        lm.requestSingleUpdate(it, /*8000L, 1000f,*/ ll, Looper.getMainLooper())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lm.registerGnssNavigationMessageCallback(gnssCb, Handler())
        }
    }

    private val ll = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            log("111 onLocationChanged")
            binding.locationInfo.append("onLocationChanged. ${location2String(location)}\n\n")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            log("222 onStatusChanged")
            binding.locationInfo.append("onStateChange $provider, $status, $extras")
        }

        override fun onProviderEnabled(provider: String?) {
            log("333 onProviderEnabled")
            binding.locationInfo.append("onProviderEnable. $provider")
        }

        override fun onProviderDisabled(provider: String?) {
            log("444 onProviderDisabled")
            binding.locationInfo.append("onProviderDisable. $provider")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val gnssCb = object : GnssNavigationMessage.Callback() {

        override fun onStatusChanged(status: Int) {
            binding.gnssInfo.append("\nonStatusChanged ${state2String(status)}")
        }

        override fun onGnssNavigationMessageReceived(event: GnssNavigationMessage?) {
            binding.gnssInfo.append("\nGnss message: $event")
        }
    }

    private fun state2String(status: Int): String {
        return when (status) {
            GnssNavigationMessage.Callback.STATUS_READY -> "ready"
            GnssNavigationMessage.Callback.STATUS_NOT_SUPPORTED -> "not support"
            GnssNavigationMessage.Callback.STATUS_LOCATION_DISABLED -> "location disabled"
            else -> "unknown"
        }
    }

    private fun location2String(loc: Location?): String {
        if (loc == null) return "NULL."
        val s = StringBuilder()
        s.append("Location : ${loc.provider}\n")
        s.append(String.format("经纬度: %.6f,%.6f\n", loc.latitude, loc.longitude))
        if (loc.hasAccuracy())
            s.append(String.format("精确度 %.0f (米)", loc.accuracy))
        return s.toString()
    }

    override fun onStop() {
        super.onStop()
        lm.removeUpdates(ll)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lm.unregisterGnssNavigationMessageCallback(gnssCb)
        }
    }
}