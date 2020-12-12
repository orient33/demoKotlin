package com.example.imagetest

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.kotlindemo.R
import kotlinx.android.synthetic.main.fragment_image1.*

class Image1Fragment : Fragment() {
    companion object {
        const val KEY_LM = "layoutManager"
        const val KEY_LOCAL = "localImage"
        const val KEY_RESET = "resetVH"
        const val KEY_ROUND = "round"
        const val KEY_CARD_VIEW = "cardView"
        const val KEY_IGNORE_GIF = "ignore_gif"
        const val KEY_PAGE = "page"
        const val KEY_PAGE_SIZE = "page-size"
    }

    private lateinit var viewModel: ImageVM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(ImageVM::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stag = getBooleanArg(KEY_LM)
        val lm: RecyclerView.LayoutManager =
            if (stag) StaggeredGridLayoutManager(
                2,
                LinearLayoutManager.VERTICAL
            )
            else GridLayoutManager(context, 2)

        recyclerView.layoutManager = lm
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.set(25, 10, 25, 10)
            }

        })
        val adapter = ImageAdapter(
            view.context, getBooleanArg(KEY_RESET),
            getBooleanArg(KEY_ROUND),
            getBooleanArg(KEY_CARD_VIEW),
            stag
        )
        recyclerView.adapter = adapter
        viewModel.getImageData(
            getBooleanArg(KEY_LOCAL),
            getBooleanArg(KEY_IGNORE_GIF),
            arguments?.getInt(KEY_PAGE_SIZE),
            arguments?.getString(KEY_PAGE)
        )
            .observe(viewLifecycleOwner, Observer<List<ImageData>> {
                adapter.setData(it)
            })
    }

    private fun getBooleanArg(key: String): Boolean {
        val args = arguments ?: return true
        return args.getBoolean(key, true)
    }
}