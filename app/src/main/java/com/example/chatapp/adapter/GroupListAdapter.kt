package com.example.chatapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import com.example.chatapp.model.pojo.create_group.Group
import kotlinx.android.synthetic.main.item_chat_user.view.*

class GroupListAdapter(private val groupList: MutableList<Group>,
                       private val context: Context): RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder>()    {


    inner class GroupListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListViewHolder {
        return GroupListViewHolder(parent.inflate(R.layout.item_chat_user))
    }
    override fun getItemCount(): Int {
        return groupList.size
    }

    fun addGroup(group: Group) {
        groupList.add(group)
        notifyItemInserted(groupList.size - 1)
    }

    fun addAllGroups(groups: List<Group>) {
        groupList.addAll(0, groups)
        notifyItemRangeInserted(0, groups.size)
    }

    override fun onBindViewHolder(holder: GroupListAdapter.GroupListViewHolder, position: Int) {

        val group = groupList[position]
        holder.itemView.apply {
            chat_user_name_txt.text = group.groupName
        }

    }
}