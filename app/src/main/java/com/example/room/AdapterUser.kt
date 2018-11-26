package com.example.room

import android.content.Context
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AdapterUser(val context: Context) : ListAdapter<User, AdapterUser.ViewHolder>(ItemDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1,
                parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { user ->
            with(holder) {
                bind(user)
            }
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView? = null
        fun bind(u: User) {
            text = itemView.findViewById(android.R.id.text1)
            text?.text = u.uuid
        }
    }
}