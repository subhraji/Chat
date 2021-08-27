package com.example.chatapp.model.repo.update_profile

import com.example.chatapp.model.pojo.update_profile.UpdateProfileResponse
import com.example.chatapp.model.repo.Outcome

interface UpdateProfileRepository {
    suspend fun updateProfile(username: String,
                              phoneno: String,
                              avatar: String,
                              newPhoneno: String,
                              token: String): Outcome<UpdateProfileResponse>

}