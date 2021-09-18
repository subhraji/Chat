package com.example.chatapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_chat_user.view.*

class ChatUserAdapter(private val chatUserList: MutableList<ChatUser>,
                      private val context: Context,
                      private val itemClickListener: ChatUserItemClickClickLister,
                      ): RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder>()   {
    inner class ChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        return ChatUserViewHolder(parent.inflate(R.layout.item_chat_user))
    }
    override fun getItemCount(): Int {
        return chatUserList.size
    }
    fun addUser(chatUser: ChatUser) {
        chatUserList.add(chatUser)
        notifyItemInserted(chatUserList.size - 1)
    }

    fun addAllUsers(chatUsers: List<ChatUser>) {
        chatUserList.addAll(0, chatUsers)
        notifyItemRangeInserted(0, chatUsers.size)
    }
    override fun onBindViewHolder(holder: ChatUserAdapter.ChatUserViewHolder, position: Int) {

        val users = chatUserList[position]
        holder.itemView.apply {
            chat_user_name_txt.text = users.userName
            chat_user_msg_txt.text = users.message

            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
            Glide.with(this).load(users.image.toString()).apply(options)
                .into(chat_user_img)

            holder.itemView.chatUserListItemRootLay.tag = users
            PushDownAnim.setPushDownAnimTo(holder.itemView.chatUserListItemRootLay).setOnClickListener {
                itemClickListener.onItemClicked(it, holder.layoutPosition)
            }

        }

    }

    interface ChatUserItemClickClickLister {
        fun onItemClicked(view: View, position: Int)
    }
}