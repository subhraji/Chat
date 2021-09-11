package com.example.chatapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import com.example.chatapp.model.pojo.create_group.Group
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.group_list_item.view.*
import kotlinx.android.synthetic.main.item_chat_user.view.*

class GroupListAdapter(private val groupList: MutableList<Group>,
                       private val context: Context,
                       private val itemClickListener: GroupItemClickClickLister
): RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder>()    {


    inner class GroupListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListViewHolder {
        return GroupListViewHolder(parent.inflate(R.layout.group_list_item))
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

            group_list_user_name_txt.text = group.groupName

            holder.itemView.groupListItemRootLay.tag = group
            PushDownAnim.setPushDownAnimTo(holder.itemView.groupListItemRootLay).setOnClickListener {
                itemClickListener.onItemClicked(it, holder.layoutPosition)
            }
        }

    }


    interface GroupItemClickClickLister {
        fun onItemClicked(view: View, position: Int)
    }
}