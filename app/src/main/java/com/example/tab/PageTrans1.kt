package com.example.tab

import android.view.View
import androidx.viewpager2.widget.ViewPager2

//https://www.jianshu.com/p/711bf2b438aa
class PageTrans1 : ViewPager2.PageTransformer {
    private val DEFAULT_MIN_SCALE = 0.85f
    private val DEFAULT_CENTER = 0.5f

    private val mMinScale = DEFAULT_MIN_SCALE

    override fun transformPage(page: View, position: Float) {
//        log("PageTransformer. $position")        // -1 , -0.55833334, 0,  0.44166666,  1
        val pageWidth = page.width
        val pageHeight = page.height
        //动画锚点设置为View中心
        //动画锚点设置为View中心
        page.pivotX = (pageWidth / 2).toFloat()
        page.pivotY = (pageHeight / 2).toFloat()
        if (position < -1) {
            //屏幕左侧不可见时
            page.scaleX = mMinScale
            page.scaleY = mMinScale
            page.pivotY = (pageWidth / 2).toFloat()
        } else if (position <= 1) {
            if (position < 0) {
                //屏幕左侧
                //(-1， 0)
                val scaleFactor: Float = (1 + position) * (1 - mMinScale) + mMinScale
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.pivotX = pageWidth.toFloat()
            } else {
                //屏幕右侧
                //(1,0)
                val scaleFactor: Float = (1 - position) * (1 - mMinScale) + mMinScale
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.pivotX = pageWidth * ((1 - position) * DEFAULT_CENTER)
            }
        } else {
            //屏幕右侧不可见
            page.pivotX = 0f
            page.scaleY = mMinScale
            page.scaleY = mMinScale
        }
    }
}