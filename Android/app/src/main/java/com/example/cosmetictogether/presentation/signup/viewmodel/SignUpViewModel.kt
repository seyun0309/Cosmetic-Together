package com.example.cosmetictogether.presentation.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.model.SignUpRequest
import com.example.cosmetictogether.data.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    private val _signUpResult = MutableLiveData<String>()
    val signUpResult: LiveData<String> get() = _signUpResult

    fun signUp(userName: String, email: String, password: String, phone: String, nickname: String, address: String) {
        val signUpRequest = SignUpRequest(userName, email, password, phone, nickname, address)

        // Use RetrofitClient to access the AuthRetrofitInterface
        RetrofitClient.apiService.signUp(signUpRequest).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                _signUpResult.postValue("회원가입 실패: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    _signUpResult.postValue("회원가입 성공!")
                } else {
                    _signUpResult.postValue("회원가입 실패: ${response.message()}")
                }
            }
        })
    }
}
