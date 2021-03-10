package com.example.imagetest

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.log

class RotateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val f = Image1Fragment()
            f.arguments = Bundle().apply {
                putBoolean(Image1Fragment.KEY_LM, false)
                putBoolean(Image1Fragment.KEY_LOCAL, false)
                putBoolean(Image1Fragment.KEY_CARD_VIEW, true)
                putBoolean(Image1Fragment.KEY_IGNORE_GIF, true)
                putInt(Image1Fragment.KEY_PAGE_SIZE, 3)
            }
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, f).commit()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log("$this,onConfigurationChanged,orient= ${newConfig.orientation}")
    }
}