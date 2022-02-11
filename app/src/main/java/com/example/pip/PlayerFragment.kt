package com.example.pip

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.kotlindemo.R

private const val ARG_PATH = "path"

/**
 * A simple [Fragment] subclass.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var path: String? = null
    private var mPlayer: IPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            path = it.getString(ARG_PATH)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val p = EPlayerImpl().createPlayer()
        p.setView(view.findViewById(R.id.surfaceView))
        if (path.isNullOrEmpty()) return
        p.play(path!!)
        mPlayer = p
    }

    override fun onDestroyView() {
        mPlayer?.release()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(path: String) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PATH, path)
                }
            }
    }
}