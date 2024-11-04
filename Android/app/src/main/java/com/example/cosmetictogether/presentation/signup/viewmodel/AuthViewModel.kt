package com.example.cosmetictogether.presentation.signup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.AuthRetrofitInterface
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
    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    private val _sendEmailResponse = MutableLiveData<SendEmailResponse?>()
    val sendEmailResponse: LiveData<SendEmailResponse?> get() = _sendEmailResponse

    private val _sendVerifiedResponse = MutableLiveData<SendVerifiedResponse?>()
    val sendVerifiedResponse: LiveData<SendVerifiedResponse?> get() = _sendVerifiedResponse

    fun login(email: String, password: String) {
        val request = LoginRequest(email, password)
        RetrofitClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                } else {
                    _loginResponse.value = null // Handle login failure
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResponse.value = null // Handle error
            }
        })
    }

    fun sendVerificationCode(email: String) {
        // Creating a request object with the email
        val request = EmailRequest(email) // Assuming EmailRequest is the expected request class for sending the verification code
        RetrofitClient.apiService.sendVerificationCode(request).enqueue(object : Callback<SendEmailResponse> {
            override fun onResponse(call: Call<SendEmailResponse>, response: Response<SendEmailResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _sendEmailResponse.value = it // Set the response body
                    } ?: run {
                        // Handle null response body
                        _sendEmailResponse.value = null
                        // 로그에 오류 메시지 추가
                        Log.e("API 오류", "응답 본문이 null입니다.")
                    }
                } else {
                    // 오류 응답 처리
                    _sendEmailResponse.value = null
                    // 오류 코드를 로그에 기록
                    Log.e("API 오류", "오류 코드: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SendEmailResponse>, t: Throwable) {
                // 네트워크 또는 다른 오류 처리
                _sendEmailResponse.value = null
                // 오류 메시지를 로그에 기록
                Log.e("API 실패", t.message ?: "알 수 없는 오류")
            }
        })
    }


    fun checkVerificationCode(email: String, authCode: String) {
        // Creating a request object for verification
        val request = VerificationRequest(email, authCode) // Assuming VerificationRequest is the expected request class
        RetrofitClient.apiService.checkVerificationCode(request).enqueue(object : Callback<SendVerifiedResponse> {
            override fun onResponse(call: Call<SendVerifiedResponse>, response: Response<SendVerifiedResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _sendVerifiedResponse.value = it // Set the response body
                    } ?: run {
                        _sendVerifiedResponse.value = null // Handle null response body
                    }
                } else {
                    _sendVerifiedResponse.value = null // Handle error
                }
            }

            override fun onFailure(call: Call<SendVerifiedResponse>, t: Throwable) {
                _sendVerifiedResponse.value = null // Handle error
            }
        })
    }

}
