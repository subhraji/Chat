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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.chatapp.R
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.loadImg
import com.example.chatapp.helper.visible
import com.example.chatapp.model.pojo.group_chat.GroupMessage
import com.example.chatapp.view.activity.PDFViewActivity
import com.example.chatapp.view.fragment.FriendsChatImagePreviewFragment
import org.jetbrains.anko.startActivity
import java.util.*

class GroupMessageListAdapter(private val messageList: MutableList<GroupMessage>,
                              private val fragmentManager: FragmentManager):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "GroupMessageListAdapter"
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.isSender) {
            // If the current user is the sender of the message
            GroupMessageListAdapter.VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            GroupMessageListAdapter.VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        Log.i("check","reached here")

        if (viewType == GroupMessageListAdapter.VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_me, parent, false)
            return GroupMessageListAdapter.SentMessageHolder(view)
        } else if (viewType == GroupMessageListAdapter.VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_other, parent, false)
            return GroupMessageListAdapter.ReceivedMessageHolder(view)
        }
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_me, parent, false)
        return GroupMessageListAdapter.SentMessageHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder.itemViewType) {
            GroupMessageListAdapter.VIEW_TYPE_MESSAGE_SENT -> {
                (holder as GroupMessageListAdapter.SentMessageHolder).bind(message, holder.itemView.context, fragmentManager, holder)
            }
            GroupMessageListAdapter.VIEW_TYPE_MESSAGE_RECEIVED -> {
                (holder as GroupMessageListAdapter.ReceivedMessageHolder).bind(message, holder.itemView.context, fragmentManager, holder)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessage(message: GroupMessage) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    fun addAllMessages(messages: List<GroupMessage>) {
        messageList.addAll(0, messages)
        notifyItemRangeInserted(0, messages.size)
    }


    private class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageText: TextView = itemView.findViewById(R.id.text_chat_message_me)
        val messageImg: ImageView = itemView.findViewById(R.id.chat_img_view_me)
        val timeText: TextView = itemView.findViewById(R.id.text_chat_timestamp_me)
        val sentMark: ImageView = itemView.findViewById(R.id.sent_mark_img)
        val dateText: TextView = itemView.findViewById(R.id.text_chat_date_me)
        val deleteChatBtn: ImageView = itemView.findViewById(R.id.delete_chat_me_btn)
        val chat_me_root_lay: LinearLayout = itemView.findViewById(R.id.chat_me_root_lay)
        val image_progress_me: ProgressBar = itemView.findViewById(R.id.image_progress_me)

        fun bind(
            message: GroupMessage,
            context: Context,
            fragmentManager: FragmentManager,
            holder: RecyclerView.ViewHolder
        ) {

            deleteChatBtn.gone()

            chat_me_root_lay.setOnLongClickListener {
                deleteChatBtn.visible()
                true
            }

            /*deleteChatBtn.tag = message
            PushDownAnim.setPushDownAnimTo(deleteChatBtn).setOnClickListener {
                chatDeleteClickListener.onDeleteClicked(it, holder.layoutPosition)
                deleteChatBtn.gone()
            }*/

            if(message.message == ""){
                messageText.gone()
            }else{
                messageText.visible()
            }

            messageText.text = message.message
            if (message.media != null) {
                if (message.messageType == "image") {
                    messageImg.visible()
                    image_progress_me.visible()
                    Glide.with(context)
                        .load(message.media)
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
                            bundle.putString("image", message.media)
                            val dialogFragment = FriendsChatImagePreviewFragment(null)
                            dialogFragment.arguments = bundle
                            dialogFragment.show(fragmentManager, "signature")
                        }
                    }
                } else if(message.messageType == "pdf"){
                    messageImg.loadImg(R.drawable.greyblack_pdf, context)
                    messageImg.setOnClickListener {
                        if (message.messageType == "pdf") {
                            context.startActivity<PDFViewActivity>(
                                "pdfUrl" to message.media,
                                "pdfName" to message.fileName
                            )
                        }
                    }
                } else {
                    messageImg.gone()
                    image_progress_me.gone()
                }
            }


            // Format the stored timestamp into a readable String using method.
            /*val date = Date(message.sentOn)
            timeText.text = date.getTimeOnly()*/

            /*if(message.isSeen == true){
                sentMark.setColorFilter(ContextCompat.getColor(context, R.color.sentMarkSeenClr), android.graphics.PorterDuff.Mode.SRC_IN)
                sentMark.setImageDrawable(context.getResources().getDrawable(R.drawable.done_all_grey))
            }else if(message.isSent == true){

                sentMark.setColorFilter(ContextCompat.getColor(context, R.color.sentMarkSentClr), android.graphics.PorterDuff.Mode.SRC_IN)

            }else{
                sentMark.setColorFilter(ContextCompat.getColor(context, R.color.sentMarkSentClr), android.graphics.PorterDuff.Mode.SRC_IN)
                sentMark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_access_time_24))
            }*/
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


        fun bind(message: GroupMessage, context: Context,
                 fragmentManager: FragmentManager,
                 holder: RecyclerView.ViewHolder
        ) {
            messageImg.gone()
            image_progress_other.gone()
            chatOtherDeleteBtn.gone()

            chatOtherRootLay.setOnLongClickListener {
                chatOtherDeleteBtn.visible()
                true
            }

            /*chatOtherDeleteBtn.tag = message
            PushDownAnim.setPushDownAnimTo(chatOtherDeleteBtn).setOnClickListener {
                chatDeleteClickListener.onDeleteClicked(it, holder.layoutPosition)
                chatOtherDeleteBtn.gone()
            }*/

            if (message.message == "") {
                messageText.gone()
            } else {
                messageText.visible()
            }
            messageText.text = message.message


            /*val date = Date(message.sentOn)
            // Format the stored timestamp into a readable String using method.
            timeText.text = date.getTimeOnly()
            dateText.text = "date"*/

            //nameText.text = message.username;
            // Insert the profile image from the URL into the ImageView.
            /*if (message.image!!.isNotBlank()) profileImage.loadUrl(
                message.image,
                context
            )*/

            if (message.media != null) {
                if (message.messageType == "image") {
                    messageImg.visible()
                    image_progress_other.visible()
                    Glide.with(context)
                        .load(message.media)
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
                            bundle.putString("image", message.media)
                            val dialogFragment = FriendsChatImagePreviewFragment(null)
                            dialogFragment.arguments = bundle
                            dialogFragment.show(fragmentManager, "signature")
                        }
                    }
                }else if(message.messageType == "pdf"){
                    messageImg.visible()
                    messageImg.loadImg(R.drawable.greyblack_pdf, context)
                    messageImg.setOnClickListener {
                        if (message.messageType == "pdf") {
                            context.startActivity<PDFViewActivity>(
                                "pdfUrl" to message.media,
                                "pdfName" to message.fileName
                            )
                        }
                    }
                } else {
                    messageImg.gone()
                    image_progress_other.gone()
                }
            }
        }
    }

}