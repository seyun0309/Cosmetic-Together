package com.example.cosmetictogether.data.api

interface LoginView {
    fun onSignInSuccess(token: String)  // 로그인 성공 시 호출되는 메서드
    fun onSignInFailure()  // 로그인 실패 시 호출되는 메서드

}