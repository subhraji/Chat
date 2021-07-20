package com.example.chatapp.adapter

import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import com.example.chatapp.model.pojo.sync_contacts.User
import kotlinx.android.synthetic.main.contact_list_item.view.*

class ContactListAdapter(private val userList: List<User>): RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>()  {

    inner class ContactListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        return ContactListViewHolder(parent.inflate(R.layout.contact_list_item))
    }
    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ContactListAdapter.ContactListViewHolder, position: Int) {
        val users = userList[position]
        holder.itemView.apply {
            user_name_txt.text = users.phoneno
        }
    }
}