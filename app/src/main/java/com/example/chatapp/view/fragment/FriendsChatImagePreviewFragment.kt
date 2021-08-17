package com.example.chatapp.view.fragment

import android.app.Dialog
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.helper.UploadImageListener
import com.example.chatapp.helper.gone
import kotlinx.android.synthetic.main.fragment_friends_chat_image_preview.*
import java.io.File
import java.net.URI


class FriendsChatImagePreviewFragment(uploadImageListener: UploadImageListener?) : DialogFragment() {

    private val uploadListener = uploadImageListener
    private var imagePath: String? = null
    private var imagePath2: String? = null
    lateinit var sendImagePathVar: String
    lateinit var imagePathUri: Uri


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

        Log.i("imagePath", imagePath.toString())

        if (imagePath != null){

            imagePathUri = Uri.parse(imagePath)
            friends_chat_imageView.setImageURI(imagePathUri)


            val path = getRealPathFromUri(imagePathUri)
            sendImagePathVar = File(path).toString()

            Log.i("filePath", sendImagePathVar)
        }

        if (imagePath2 != null){
            friends_chat_img_controlLayout.gone()
            chat_img_top_layout.gone()

            Glide.with(requireActivity())
                .load(File(imagePath2!!)) // Uri of the picture
                .into(friends_chat_imageView)

        }

        back_btn.setOnClickListener {
            dismiss()
        }

        friends_chat_caption_img_btnSend.setOnClickListener {
            val caption = friends_chat_img_caption.text.toString().trim()
            sendImagePathVar?.let { path ->
                uploadListener?.uploadImage(path, caption)
            }
            dismiss()
        }
    }

    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = requireContext().contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
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