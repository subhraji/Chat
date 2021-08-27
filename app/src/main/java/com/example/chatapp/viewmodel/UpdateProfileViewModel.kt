package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.repo.update_profile.UpdateProfileRepository

class UpdateProfileViewModel(private val mUpdateProfileRepository: UpdateProfileRepository): ViewModel() {

    fun updateProfile(username: String, phoneno: String, avatar: String, newPhoneno: String,token: String) = liveData {
        emit(mUpdateProfileRepository.updateProfile(username,phoneno, avatar, newPhoneno, token))
    }
}