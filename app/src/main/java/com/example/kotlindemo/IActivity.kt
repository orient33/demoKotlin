package com.example.kotlindemo

import android.os.Bundle
import androidx.fragment.app.Fragment

interface IActivity {
    fun toFragment(fragment: Fragment)
    fun toFragment(fragmentName: String) {}
    fun toFragmentWithArgs(fragmentName: String, args: Bundle) {}
}