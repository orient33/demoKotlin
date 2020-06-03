package com.example.kotlindemo

import androidx.fragment.app.Fragment

interface IActivity {
    fun toFragment(fragment: Fragment)
    fun toFragment(fragmentName: String) {}
}