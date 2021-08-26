package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.repo.get_profile.GetProfileRepository

class GetProfileViewModel(private val mGetProfileRepository: GetProfileRepository): ViewModel() {

    fun getProfile(token: String) = liveData {
        emit(mGetProfileRepository.getProfile(token))
    }

}