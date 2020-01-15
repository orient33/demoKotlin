package com.example.kotlindemo

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.example.toast
import kotlinx.android.synthetic.main.test_round_image_view.*

/**
 * @author dundongfang on 2018/4/26.
 */
@Keep
class TestFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.test_round_image_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setXfMode(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                toast(parent!!.context, "onNothingSelected!")
            }

        }
    }

    private fun setXfMode(position: Int) {
        val mode: PorterDuff.Mode
        when (position) {
            0 -> mode = PorterDuff.Mode.CLEAR
            1 -> mode = PorterDuff.Mode.SRC
            2 -> mode = PorterDuff.Mode.DST
            3 -> mode = PorterDuff.Mode.SRC_OVER
            4 -> mode = PorterDuff.Mode.DST_OVER
            5 -> mode = PorterDuff.Mode.SRC_IN
            6 -> mode = PorterDuff.Mode.DST_IN
            7 -> mode = PorterDuff.Mode.SRC_OUT
            8 -> mode = PorterDuff.Mode.DST_OUT
            9 -> mode = PorterDuff.Mode.SRC_ATOP
            10 -> mode = PorterDuff.Mode.DST_ATOP
            11 -> mode = PorterDuff.Mode.XOR
            12 -> mode = PorterDuff.Mode.DARKEN
            13 -> mode = PorterDuff.Mode.LIGHTEN
            14 -> mode = PorterDuff.Mode.MULTIPLY
            15 -> mode = PorterDuff.Mode.SCREEN
            else -> throw IllegalArgumentException("position error. $position")
        }
        toast(view!!.context, "select $position , Mode $mode")
        riv.setXfMode(mode)
    }
}