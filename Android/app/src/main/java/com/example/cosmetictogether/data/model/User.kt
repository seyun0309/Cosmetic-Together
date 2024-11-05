package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("nickName") val nick: String,
    @SerializedName("role") val role: String
)

data class EmailRequest(
    val email: String // 이메일 주소를 저장하는 프로퍼티
)

data class SendEmailResponse(
    @SerializedName("Confirmation : ") val confirmation: String?
)

data class VerificationRequest(
    val email: String,  // 이메일 주소
    val authCode: String // 인증 코드
)

data class SendVerifiedResponse(
    @SerializedName("success") val success: Boolean,  // 인증 성공 여부
    @SerializedName("responseMessage") val responseMessage: String  // 응답 메시지
)

data class SignUpRequest( // 회원가입
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("address") val address: String
)

data class SignUpResponse(
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String
)

data class NicknameCheckRequest(
    val nickname: String
)

data class NicknameCheckResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)