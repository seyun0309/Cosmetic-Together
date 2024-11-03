package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.AuthResponse
import com.example.cosmetictogether.data.model.SendEmailResponse
import com.example.cosmetictogether.data.model.SendVerifiedResponse
import com.example.cosmetictogether.data.model.SignInRequest
import com.example.cosmetictogether.data.model.EmailRequest
import com.example.cosmetictogether.data.model.VerificationRequest
import com.example.cosmetictogether.data.model.SignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthRetrofitInterface {
    @POST("api/v1/login")
    fun login(
        @Body SignInRequest: SignInRequest
    ): Call<AuthResponse>

    @POST("api/v1/email/code")
    fun sendEmail(
        @Body request: EmailRequest
    ): Call<SendEmailResponse>

    @POST("api/v1/emailCheck")
    fun sendVerified(
        @Body request: VerificationRequest
    ): Call<SendVerifiedResponse>

    @POST("api/v1/signup")
    fun signup(
        @Query("userName") userName: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("phone") phone: String,
        @Query("nickname") nickname: String,
        @Query("address") address: String,
    ): Call<SignUpResponse>
}