package com.example.chatapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.inflate
import com.example.chatapp.helper.visible
import com.example.chatapp.model.pojo.group_member.User
import kotlinx.android.synthetic.main.group_member_list_item.view.*

class GroupMemberListAdapter(private val userList: List<User>, private val context: Context): RecyclerView.Adapter<GroupMemberListAdapter.GroupMemberViewHolder>() {

    inner class GroupMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberViewHolder {
        return GroupMemberViewHolder(parent.inflate(R.layout.group_member_list_item))
    }
    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: GroupMemberListAdapter.GroupMemberViewHolder, position: Int) {

        val users = userList[position]
        holder.itemView.apply {

            if(!users.userGroup.isAdmin){
                is_admin_txt.gone()
            }else{
                is_admin_txt.visible()
            }

            group_member_name_txt.text = users.phoneno
        }
    }
}