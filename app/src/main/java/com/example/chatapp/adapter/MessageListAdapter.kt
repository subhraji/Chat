package com.example.chatapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.helper.getTimeOnly
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.visible
import com.example.chatapp.view.fragment.FriendsChatImagePreviewFragment
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.item_chat_user.view.*
import java.util.*
import javax.sql.DataSource

class MessageListAdapter(private val messageList: MutableList<Message>,
                         private val fragmentManager: FragmentManager,
                         private val chatDeleteClickListener: ChatDeleteClickListener):
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
                (holder as SentMessageHolder).bind(message, holder.itemView.context, fragmentManager, holder, chatDeleteClickListener)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                (holder as ReceivedMessageHolder).bind(message, holder.itemView.context, fragmentManager, holder, chatDeleteClickListener)
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

    fun removeMessage(message: Message) {
        messageList.remove(message)
    }

    fun updateSent(msgUuid:String){
        messageList?.find { it.msgUuid == msgUuid }?.isSent = true
        notifyDataSetChanged()
    }

    fun updateSeen(msgUuid:String){
        messageList?.find { it.msgUuid == msgUuid }?.isSeen = true
        notifyDataSetChanged()
    }

    private class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageText: TextView = itemView.findViewById(R.id.text_chat_message_me)
        val messageImg: ImageView = itemView.findViewById(R.id.chat_img_view_me)
        val timeText: TextView = itemView.findViewById(R.id.text_chat_timestamp_me)
        val sentMark: ImageView = itemView.findViewById(R.id.sent_mark_img)
        val dateText: TextView = itemView.findViewById(R.id.text_chat_date_me)
        val deleteChatBtn: ImageView = itemView.findViewById(R.id.delete_chat_me_btn)
        val chat_me_root_lay:LinearLayout = itemView.findViewById(R.id.chat_me_root_lay)
        val image_progress_me:ProgressBar = itemView.findViewById(R.id.image_progress_me)

        fun bind(
            message: Message,
            context: Context,
            fragmentManager: FragmentManager,
            holder: RecyclerView.ViewHolder,
            chatDeleteClickListener: ChatDeleteClickListener
        ) {

            deleteChatBtn.gone()

            chat_me_root_lay.setOnLongClickListener {
                deleteChatBtn.visible()
                true
            }

            deleteChatBtn.tag = message
            PushDownAnim.setPushDownAnimTo(deleteChatBtn).setOnClickListener {
                chatDeleteClickListener.onDeleteClicked(it, holder.layoutPosition)
                deleteChatBtn.gone()
            }

            if(message.msg == ""){
                messageText.gone()
            }else{
                messageText.visible()
            }

            messageText.text = message.msg
            if (message.image != null) {
                if (message.messageType == "image") {
                    messageImg.visible()
                    image_progress_me.visible()
                    Glide.with(context)
                        .load(message.image)
                        .centerCrop()
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                image_progress_me.gone()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                dataSource: com.bumptech.glide.load.DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                image_progress_me.gone()
                                return false
                            }
                        })
                        .into(messageImg)

                    messageImg.setOnClickListener {
                        if(message.messageType == "image") {
                            val bundle = Bundle()
                            bundle.putString("image", message.image)
                            val dialogFragment = FriendsChatImagePreviewFragment(null)
                            dialogFragment.arguments = bundle
                            dialogFragment.show(fragmentManager, "signature")
                        }
                    }
                } else {
                    messageImg.gone()
                    image_progress_me.gone()
            }

                // Format the stored timestamp into a readable String using method.
                val date = Date(message.sentOn)
                timeText.text = date.getTimeOnly()

                if(message.isSeen == true){
                    sentMark.setColorFilter(ContextCompat.getColor(context, R.color.sentMarkSeenClr), android.graphics.PorterDuff.Mode.SRC_IN)
                    sentMark.setImageDrawable(context.getResources().getDrawable(R.drawable.done_all_grey))
                }else if(message.isSent == true){

                    sentMark.setColorFilter(ContextCompat.getColor(context, R.color.sentMarkSentClr), android.graphics.PorterDuff.Mode.SRC_IN)

                }else{
                    sentMark.setColorFilter(ContextCompat.getColor(context, R.color.sentMarkSentClr), android.graphics.PorterDuff.Mode.SRC_IN)
                    sentMark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_access_time_24))
                }
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
        val chatOtherRootLay: LinearLayout = itemView.findViewById(R.id.chat_other_root_lay)
        val chatOtherDeleteBtn: ImageView = itemView.findViewById(R.id.delete_chat_other_btn)
        val image_progress_other:ProgressBar = itemView.findViewById(R.id.image_progress_other)


        fun bind(message: Message, context: Context,
                 fragmentManager: FragmentManager,
                 holder: RecyclerView.ViewHolder,
                 chatDeleteClickListener: ChatDeleteClickListener
        ) {
            //messageImg.gone()
            chatOtherDeleteBtn.gone()

            chatOtherRootLay.setOnLongClickListener {
                chatOtherDeleteBtn.visible()
                true
            }

            chatOtherDeleteBtn.tag = message
            PushDownAnim.setPushDownAnimTo(chatOtherDeleteBtn).setOnClickListener {
                chatDeleteClickListener.onDeleteClicked(it, holder.layoutPosition)
                chatOtherDeleteBtn.gone()
            }

            if (message.msg == "") {
                messageText.gone()
            } else {
                messageText.visible()
            }
            messageText.text = message.msg
            val date = Date(message.sentOn)
            // Format the stored timestamp into a readable String using method.
            timeText.text = date.getTimeOnly()
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
                    image_progress_other.visible()
                    Glide.with(context)
                        .load(message.image)
                        .centerCrop()
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                image_progress_other.gone()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                dataSource: com.bumptech.glide.load.DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                image_progress_other.gone()
                                return false
                            }
                        })
                        .into(messageImg)

                    messageImg.setOnClickListener {
                        if (message.messageType == "image") {
                            val bundle = Bundle()
                            bundle.putString("image", message.image)
                            val dialogFragment = FriendsChatImagePreviewFragment(null)
                            dialogFragment.arguments = bundle
                            dialogFragment.show(fragmentManager, "signature")
                        }
                    }
                } else {
                    messageImg.gone()
                    image_progress_other.gone()
                }
            }
        }
    }


    interface ChatDeleteClickListener {
        fun onDeleteClicked(view: View, position: Int)
    }

}