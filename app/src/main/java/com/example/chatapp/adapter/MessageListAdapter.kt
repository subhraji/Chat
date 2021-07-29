package com.example.chatapp.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.visible
import com.example.chatapp.view.fragment.FriendsChatImagePreviewFragment
import java.util.*

class MessageListAdapter(private val messageList: MutableList<Message>, private val fragmentManager: FragmentManager):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "MessageListAdapter"
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.isSender) {
            // If the current user is the sender of the message
            VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        Log.i("check","reached here")

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_me, parent, false)
            return SentMessageHolder(view)
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_other, parent, false)
            return ReceivedMessageHolder(view)
        }

        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_me, parent, false)
        return SentMessageHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                (holder as SentMessageHolder).bind(message, holder.itemView.context, fragmentManager)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                (holder as ReceivedMessageHolder).bind(message, holder.itemView.context, fragmentManager)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessage(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    fun addAllMessages(messages: List<Message>) {
        messageList.addAll(0, messages)
        notifyItemRangeInserted(0, messages.size)
    }

    private class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageText: TextView = itemView.findViewById(R.id.text_chat_message_me)
        val messageImg: ImageView = itemView.findViewById(R.id.chat_img_view_me)
        val timeText: TextView = itemView.findViewById(R.id.text_chat_timestamp_me)
        val dateText: TextView = itemView.findViewById(R.id.text_chat_date_me)

        fun bind(message: Message, context: Context, fragmentManager: FragmentManager) {
            if(message.message == ""){
                messageText.gone()
            }else{
                messageText.visible()
            }

            messageText.text = message.message
            if (message.image != null) {
                if (message.messageType == "image") {
                    messageImg.visible()
                    //messageImg.loadUrl(message.image, context)
                    messageImg.setOnClickListener {
                        if(message.messageType == "image") {
                            val bundle = Bundle()
                            bundle.putString("image", message.image)
                            val dialogFragment = FriendsChatImagePreviewFragment()
                            dialogFragment.arguments = bundle
                            dialogFragment.show(fragmentManager, "signature")
                        }
                    }
                } else {
                messageImg.gone()
            }

            val date = Date(message.createdAt)
            // Format the stored timestamp into a readable String using method.
            timeText.text = "20"
            dateText.text = "20"
        }
        }
    }

    private class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.text_gchat_message_other)
        val timeText: TextView = itemView.findViewById(R.id.text_gchat_timestamp_other)
        val nameText: TextView = itemView.findViewById(R.id.text_gchat_user_other)
        val profileImage: ImageView = itemView.findViewById(R.id.image_gchat_profile_other)
        val messageImg: ImageView = itemView.findViewById(R.id.chat_img_view_other)
        val dateText: TextView = itemView.findViewById(R.id.text_chat_date_other)

        fun bind(message: Message, context: Context, fragmentManager: FragmentManager) {
            if (message.message == "") {
                messageText.gone()
            } else {
                messageText.visible()
            }
            messageText.text = message.message
            val date = Date(message.createdAt)
            // Format the stored timestamp into a readable String using method.
            timeText.text = "time"
            dateText.text = "date"
            //nameText.text = message.username;
            // Insert the profile image from the URL into the ImageView.
            /*if (message.image!!.isNotBlank()) profileImage.loadUrl(
                message.image,
                context
            )*/

            if (message.image != null) {
                if (message.messageType == "image") {
                    messageImg.visible()
                    //messageImg.loadUrl(message.image, context)
                    messageImg.setOnClickListener {
                        if (message.messageType == "image") {
                            val bundle = Bundle()
                            bundle.putString("image", message.image)
                            val dialogFragment = FriendsChatImagePreviewFragment()
                            dialogFragment.arguments = bundle
                            dialogFragment.show(fragmentManager, "signature")
                        }
                    }
                } else {
                    messageImg.gone()
                }
            }
        }
    }
}