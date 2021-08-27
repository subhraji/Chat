package com.example.chatapp.model.network

import com.example.chatapp.model.pojo.create_group.CreateGroupRequest
import com.example.chatapp.model.pojo.create_group.CreateGroupResponse
import com.example.chatapp.model.pojo.friend_chat.UploadImageResponse
import com.example.chatapp.model.pojo.get_profile.GetProfile
import com.example.chatapp.model.pojo.req_otp.ReqOtpParam
import com.example.chatapp.model.pojo.req_otp.RequestOtp
import com.example.chatapp.model.pojo.sync_contacts.SyncContactsReq
import com.example.chatapp.model.pojo.sync_contacts.SyncContactsResponse
import com.example.chatapp.model.pojo.update_profile.UpdateProfileRequest
import com.example.chatapp.model.pojo.update_profile.UpdateProfileResponse
import com.example.chatapp.model.pojo.verify_otp.VerifyOtpReq
import com.example.chatapp.model.pojo.verify_otp.VerifyOtpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface ApiInterface {

    @POST("/req-otp")
    suspend fun reqOtp(@Body body: ReqOtpParam?): RequestOtp?

    @POST("/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpReq?): VerifyOtpResponse?

    @POST("/api/sync-contacts")
    suspend fun syncContacts(@Body body: SyncContactsReq?,
                             @Header("Authorization") token: String): SyncContactsResponse?

    @Multipart
    @POST("/api/upload-media")
    suspend fun uploadChatImage(@Part("receiver_id") receiver_id: RequestBody,
                                @Part("message_type") message_type: RequestBody,
                                @Part image: MultipartBody.Part?,
                                @Header("Authorization") token: String): UploadImageResponse?

    @POST("/api/create-group")
    suspend fun createGroup(@Body body: CreateGroupRequest?,
                             @Header("Authorization") token: String): CreateGroupResponse?

    @GET("/api/get-user-profile")
    suspend fun getProfile(@Header("Authorization") token: String): GetProfile?

    @POST("/api/update-profile")
    suspend fun updateProfile(@Body body: UpdateProfileRequest?,
                             @Header("Authorization") token: String): UpdateProfileResponse?
}