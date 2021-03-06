package com.example.imagetest

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlindemo.IActivity
import com.example.kotlindemo.R
import com.example.log
import com.example.widget.RoundImageView

class ImageVH(root: View) : RecyclerView.ViewHolder(root), View.OnClickListener,
    View.OnLongClickListener {
    private val imageView = root.findViewById<RoundImageView>(R.id.image)
    private val nameView = root.findViewById<TextView>(R.id.title)
    private var url: String? = null

    fun bind(pos: Int, imageData: ImageData, resetLayoutParamsL: Boolean, round: Boolean) {
        var rr = Glide.with(itemView).load(imageData.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.asset)
        if (round) {
            rr = rr.transform(GlideRoundedWithBorderTransform())
        }
        rr.into(imageView)
        imageView.setDebug(pos == 2)
        val name = "($pos), ${imageData.name}"
        imageView.setOnClickListener(this)
        imageView.setOnLongClickListener(this)
        nameView.tag = "$name\n  ${imageData.des} \nreset高度 = $resetLayoutParamsL"
        nameView?.text = if (name.length > 12) name.substring(0, 12) else name
        if (resetLayoutParamsL) {
            val width = imageView.measuredWidth
            val ratio = imageData.aspRatio
            if (width > 0) {
                val height = (width * ratio).toInt()
                setImageHeight(pos, height)
            } else {
                log("$name, width = $width")//初始是width 0, 滑动后才行
            }
        }
        url = imageData.videoUrl
    }

    private fun setImageHeight(pos: Int, height: Int) {
        val lp = imageView.layoutParams
        if (lp != null) {
            if (lp.height == height) return
            lp.height = height
            imageView.layoutParams = lp
//            log("$pos, reset height $height")
        } else {
            log("lp is null")
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.image -> {
                showImageInfo(v)
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        val ctx = v.context
        if (ctx is IActivity) {
            val fragment = VideoFragment.newInstance(url);
            ctx.toFragment(fragment)
        }
        return true
    }

    private fun showImageInfo(v: View) {
        val dialog = AlertDialog.Builder(v.context)
            .setTitle("ViewHolder信息")
            .setMessage("(pos,title)=${nameView.tag}\nviewHeight=${v.measuredHeight},width=${v.measuredWidth}\n")
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .create()
        dialog.show()
    }
}