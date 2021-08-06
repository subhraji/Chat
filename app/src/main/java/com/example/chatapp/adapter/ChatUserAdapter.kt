package com.example.chatapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import com.example.chatapp.model.pojo.chat_user.ChatUser
import kotlinx.android.synthetic.main.item_chat_user.view.*

class ChatUserAdapter(private val chatUserList: List<ChatUser>, private val context: Context): RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder>()   {
    inner class ChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        return ChatUserViewHolder(parent.inflate(R.layout.item_chat_user))
    }
    override fun getItemCount(): Int {
        return chatUserList.size
    }
    /*fun addChatUser(chatUser: ChatUser) {
        chatUserList.filter {
            chatUserList.remove(it.userId == chatUser.userId)
        }
        chatUserList.add(chatUser)
        notifyItemInserted(chatUserList.size - 1)
    }

    fun addAllChatUser(chatUser: List<ChatUser>) {
        chatUserList.addAll(0, chatUser)
        notifyItemRangeInserted(0, chatUser.size)
    }*/
    override fun onBindViewHolder(holder: ChatUserAdapter.ChatUserViewHolder, position: Int) {

        val users = chatUserList[position]
        holder.itemView.apply {
            chat_user_name_txt.text = users.userName
            chat_user_msg_txt.text = users.message

            chatUserListItemRootLay.setOnClickListener {
                val bundle = bundleOf("userId" to users.userId, "phoneno" to users.userName)
                findNavController().navigate(R.id.action_mainFragment_to_chatFragment, bundle)
            }
        }

    }
}