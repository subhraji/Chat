package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.repo.ApiResponse
import com.example.chatapp.model.repo.chat_image.ChatImageUploadRepository
import okhttp3.MultipartBody

class UploadChatImageViewModel(private val chatImageUploadRepository: ChatImageUploadRepository) : ViewModel() {

    fun uploadImage(receiver_id: String,
                    messageType: String,
                    image: MultipartBody.Part?,
                    token: String) = liveData {

        emit(chatImageUploadRepository.uploadChatImage(receiver_id, messageType, image,token))

    }
}