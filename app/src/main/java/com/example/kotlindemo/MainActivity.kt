package com.example.kotlindemo

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.window.layout.WindowMetricsCalculator
import com.example.BaseActivity
import com.example.displayCut
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SimpleTimeZone

//import org.jetbrains.anko.toast

private const val fragmentId = android.R.id.content

class MainActivity : BaseActivity(), IActivity {

    lateinit var fm: FragmentManager
    lateinit var containerView: View
    lateinit var config: Configuration
    lateinit var wic: WindowInsetsControllerCompat
    var large: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        val splachScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        config = resources.configuration
        displayCut(this)
        fm = supportFragmentManager
        containerView = findViewById<View>(fragmentId)
        wic = WindowInsetsControllerCompat(window, containerView)
        if (savedInstanceState == null) {
            fm.beginTransaction().replace(fragmentId, HomeFragment()).commit()
        }
        log("onCreate.resource is $resources, $this")
//        val pi = packageManager.getPackageInfo(packageName, 0)
//        val ai = packageManager.getApplicationInfo(packageName, 0)
//        textView.text = getString(R.string.version_info, pi.versionName, pi.versionCode, ai.targetSdkVersion)
//        button.setOnClickListener {
//            Log.e("df", "click. id= =")
//        }
//        button2.text = if (pi.versionCode == 1) "抛出异常.FC...player" else "已修复FC !"
//        button2.setOnClickListener {
//            Log.e("df", "just throw Exception.")
//            if (pi.versionCode == 1) throw RuntimeException(" throw a Exception..!")
//        }
//        val list = listOf("abcdefg", "bcde")
//        list.toObservable()
//                .filter { it.length >= 5 }
//                .subscribeBy(
//                        onNext = { log("onNext() " + it) },
//                        onError = { log("onError() " + it) }
//                )
//        log("onCreate")
//
//        //kotlin标准库提供的with
//        val p = Person(10, "dongfang")
//        with(p) {
//            log("age is $age , name is $name, height is $gao")
//        }
        updateOrient()

        val current = Date(System.currentTimeMillis())
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK)
        df.timeZone = SimpleTimeZone(0, "UTC")
        val time = df.format(current)

        val date = df.parse("2018-08-14 09:00:00")

//        startActivity(Intent(this, RotateActivity::class.java))
//        log("current time : $time, $current,  parse date $date") //2018-08-01 13:00:00
// current time : 2018-08-14 10:24:34, Tue Aug 14 18:24:34 GMT+08:00 2018,  parse date Tue Aug 14 17:00:00 GMT+08:00 2018
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val diff = config.updateFrom( newConfig)
        updateOrient()
        log("onConfig Change. diff =  orientation 0x${Integer.toHexString(diff)}")
    }

    fun updateOrient() {
        val wmc = WindowMetricsCalculator.getOrCreate()
        val bounds = wmc.computeCurrentWindowMetrics(this).bounds
        val ratio = 1f * bounds.height() / bounds.width()
        large = ratio < 2f && ratio > 0.5f
        log("update orient. ratio=$ratio, large =$large,  set light status bar: ${config.orientation}")
        wic.isAppearanceLightStatusBars =
            config.orientation != Configuration.ORIENTATION_LANDSCAPE
    }

    override fun toFragment(fragmentName: String) {
        val fragment = fm.fragmentFactory.instantiate(classLoader, fragmentName)
        toFragment(fragment)
    }

    override fun toFragmentWithArgs(fragmentName: String, args: Bundle) {
        val fragment = fm.fragmentFactory.instantiate(classLoader, fragmentName)
        fragment.arguments = args
        fm.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right
            )
            .replace(fragmentId, fragment)
            .addToBackStack(fragment.toString())
            .commit()
    }

    override fun toFragment(fragment: Fragment) {
        fm.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right
            )
            .replace(fragmentId, fragment)
            .addToBackStack(fragment.toString())
            .commit()
    }

    override fun onBackPressed() {
        val f = fm.findFragmentById(fragmentId)
        if (f is IFragment) {
            if (!f.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}
