package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("accessToken") val access: String,
    @SerializedName("refreshToken") val refresh: String,
    @SerializedName("nickName") val nick: String,
    @SerializedName("role") val role: String
)

data class SendEmailResponse(
    @SerializedName("Confirmation : ")
    val confirmation: String?
)

data class SendVerifiedResponse(
    @SerializedName("Verified") val Verified: Boolean
)

data class SignUpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("detailMessage") val detailMessage: String,
)