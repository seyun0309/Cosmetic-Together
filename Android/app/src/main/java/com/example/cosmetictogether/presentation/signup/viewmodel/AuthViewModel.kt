package com.example.cosmetictogether.presentation.signup.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.EmailRequest
import com.example.cosmetictogether.data.model.LoginRequest
import com.example.cosmetictogether.data.model.LoginResponse
import com.example.cosmetictogether.data.model.SendEmailResponse
import com.example.cosmetictogether.data.model.SendVerifiedResponse
import com.example.cosmetictogether.data.model.VerificationRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {
    // 로그인 응답 데이터 저장 LiveData
    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    // 이메일 전송 응답 데이터 저장 LiveData
    private val _sendEmailResponse = MutableLiveData<SendEmailResponse?>()
    val sendEmailResponse: LiveData<SendEmailResponse?> get() = _sendEmailResponse

    // 인증 코드 검증 응답 데이터 저장 LiveData
    private val _sendVerifiedResponse = MutableLiveData<SendVerifiedResponse?>()
    val sendVerifiedResponse: LiveData<SendVerifiedResponse?> get() = _sendVerifiedResponse

    // 로그인 요청 메소드
    fun login(email: String, password: String, context: Context) {
        val request = LoginRequest(email, password) // 로그인 요청 객체 생성
        RetrofitClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) { // 응답이 성공적일 때
                    _loginResponse.value = response.body() // 응답 데이터를 _loginResponse에 저장

                    response.body()?.let { loginResponse ->
                        // 로그인 응답에 포함된 accessToken과 refreshToken을 저장
                        val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("access_token", loginResponse.accessToken) // accessToken 저장
                            putString("refresh_token", loginResponse.refreshToken) // refreshToken 저장
                            apply() // 변경 사항 저장
                        }
                    }
                } else {
                    _loginResponse.value = null // 로그인 실패 시 null 설정
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResponse.value = null // 오류 발생 시 null 설정
            }
        })
    }

    // 이메일 인증 번호 전송 요청 메소드
    fun sendVerificationCode(email: String) {
        val request = EmailRequest(email) // 이메일 요청 객체 생성
        RetrofitClient.apiService.sendVerificationCode(request).enqueue(object : Callback<SendEmailResponse> {
            override fun onResponse(call: Call<SendEmailResponse>, response: Response<SendEmailResponse>) {
                if (response.isSuccessful) { // 응답이 성공적일 때
                    response.body()?.let {
                        _sendEmailResponse.value = it // 응답 본문 데이터를 _sendEmailResponse에 저장
                    } ?: run {
                        // 응답 본문이 null일 경우 처리
                        _sendEmailResponse.value = null
                        Log.e("API 오류", "응답 본문이 null입니다.") // 로그에 오류 메시지 기록
                    }
                } else {
                    _sendEmailResponse.value = null // 오류 응답 처리
                    Log.e("API 오류", "오류 코드: ${response.code()}") // 로그에 오류 코드 기록
                }
            }

            override fun onFailure(call: Call<SendEmailResponse>, t: Throwable) {
                _sendEmailResponse.value = null // 네트워크 또는 다른 오류 발생 시 null 설정
                Log.e("API 실패", t.message ?: "알 수 없는 오류") // 로그에 오류 메시지 기록
            }
        })
    }

    // 이메일과 인증 코드를 검증하는 메소드
    fun checkVerificationCode(email: String, authCode: String) {
        val request = VerificationRequest(email, authCode) // 인증 요청 객체 생성
        RetrofitClient.apiService.checkVerificationCode(request).enqueue(object : Callback<SendVerifiedResponse> {
            override fun onResponse(call: Call<SendVerifiedResponse>, response: Response<SendVerifiedResponse>) {
                if (response.isSuccessful) { // 응답이 성공적일 때
                    response.body()?.let {
                        _sendVerifiedResponse.value = it // 응답 본문 데이터를 _sendVerifiedResponse에 저장
                    } ?: run {
                        _sendVerifiedResponse.value = null // 응답 본문이 null일 경우 처리
                    }
                } else {
                    _sendVerifiedResponse.value = null // 오류 응답 처리
                }
            }

            override fun onFailure(call: Call<SendVerifiedResponse>, t: Throwable) {
                _sendVerifiedResponse.value = null // 네트워크 또는 다른 오류 발생 시 null 설정
            }
        })
    }

}
