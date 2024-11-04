package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.LoginRequest
import com.example.cosmetictogether.data.model.LoginResponse
import com.example.cosmetictogether.data.model.EmailRequest
import com.example.cosmetictogether.data.model.VerificationRequest
import com.example.cosmetictogether.data.model.SendEmailResponse
import com.example.cosmetictogether.data.model.SendVerifiedResponse
import com.example.cosmetictogether.data.model.SignUpRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRetrofitInterface {
    @POST("/api/v1/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/v1/email/code")
    fun sendVerificationCode(@Body request: EmailRequest): Call<SendEmailResponse>

    @POST("/api/v1/emailCheck")
    fun checkVerificationCode(@Body request: VerificationRequest): Call<SendVerifiedResponse>

    @POST("/api/v1/signup")
    fun signUp(@Body signUpRequest: SignUpRequest): Call<String>
}
