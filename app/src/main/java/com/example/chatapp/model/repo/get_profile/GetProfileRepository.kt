package com.example.chatapp.model.repo.get_profile

import com.example.chatapp.model.pojo.get_profile.GetProfile
import com.example.chatapp.model.repo.Outcome

interface GetProfileRepository {
    suspend fun getProfile(token: String): Outcome<GetProfile>
}