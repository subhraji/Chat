package com.example.chatapp.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.chatapp.R
import com.example.chatapp.helper.gone
import kotlinx.android.synthetic.main.fragment_friends_chat_image_preview.*

class FriendsChatImagePreviewFragment() : DialogFragment() {

    //private val uploadListener = uploadImageListener
    private var imagePath: String? = null
    private var imagePath2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        imagePath = arguments?.getString("path")
        imagePath2 = arguments?.getString("image")

        return inflater.inflate(R.layout.fragment_friends_chat_image_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (imagePath != null){
            //imagePath?.let { friends_chat_imageView.loadImg(it, requireContext()) }
        }

        if (imagePath2 != null){
            friends_chat_img_controlLayout.gone()
            chat_img_top_layout.gone()
            //imagePath2?.let { friends_chat_imageView.loadImg(it, requireContext()) }
        }

        back_btn.setOnClickListener {
            dismiss()
        }

        friends_chat_caption_img_btnSend.setOnClickListener {
            val caption = friends_chat_img_caption.text.toString().trim()
            imagePath?.let { path ->
                //uploadListener?.uploadImage(path, caption)
            }
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}