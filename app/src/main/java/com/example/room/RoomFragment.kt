package com.example.room

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.App
import com.example.kotlindemo.R
import com.example.log
import com.example.toast
import java.util.*

class RoomFragment : Fragment(), View.OnClickListener {
    private lateinit var dao: UserDao
    private lateinit var adapter: AdapterUser
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dao = AppDb.getIns(App.sContext).userDao()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val model = ViewModelProvider(this).get(UserViewModel::class.java)
        model.getUsers().observe(viewLifecycleOwner, { users ->
            updateUI(users)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AdapterUser(view.context)
        val listView = view.findViewById<RecyclerView>(R.id.listView)
        listView.layoutManager = LinearLayoutManager(view.context)
        listView.adapter = adapter
        view.findViewById<View>(R.id.add).setOnClickListener(this)
        view.findViewById<View>(R.id.remove).setOnClickListener(this)
        view.findViewById<View>(R.id.removeAll).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add -> {
                dao.insertAll(User(UUID.randomUUID().toString()))
            }
            R.id.removeAll -> {
                dao.deleteAll()
            }
            R.id.remove -> {
                val c = context
                if (c != null)
                    toast(c, "which remove")
            }
        }
    }


    private fun updateUI(list: List<User>?) {
        log("RoomFragment. updateUI " + list?.size)
        adapter.submitList(list)
    }
}