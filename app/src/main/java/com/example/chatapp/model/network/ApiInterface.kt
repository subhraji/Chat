package com.example.chatapp.model.network

import com.example.chatapp.model.pojo.req_otp.ReqOtpParam
import com.example.chatapp.model.pojo.req_otp.RequestOtp
import com.example.chatapp.model.pojo.sync_contacts.SyncContactsReq
import com.example.chatapp.model.pojo.sync_contacts.SyncContactsResponse
import com.example.chatapp.model.pojo.verify_otp.VerifyOtpReq
import com.example.chatapp.model.pojo.verify_otp.VerifyOtpResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiInterface {

    @POST("/req-otp")
    suspend fun reqOtp(@Body body: ReqOtpParam?): RequestOtp?

    @POST("/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpReq?): VerifyOtpResponse?

    @POST("/api/sync-contacts")
    suspend fun syncContacts(@Body body: SyncContactsReq?, @Header("Authorization") token: String): SyncContactsResponse?
}