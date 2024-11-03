package com.example.cosmetictogether.data.model

data class EmailRequest(
    val email: String // 이메일 주소를 저장하는 프로퍼티
)

data class VerificationRequest(
    val email: String,  // 이메일 주소
    val authCode: String // 인증 코드
)
