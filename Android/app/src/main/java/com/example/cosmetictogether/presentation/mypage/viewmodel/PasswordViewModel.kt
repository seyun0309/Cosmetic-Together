package com.example.cosmetictogether.presentation.mypage.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.MyPageRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.CheckPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordViewModel : ViewModel() {

    private val myPageApi: MyPageRetrofitInterface =
        RetrofitClient.getInstance().create(MyPageRetrofitInterface::class.java)

    fun verifyPasswordFromServer(token: String, password: String, callback: (Boolean) -> Unit) {
        val request = CheckPasswordRequest(password)
        myPageApi.verifyPassword(token, request).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                callback(response.isSuccessful)
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                callback(false)
            }
        })
    }

    fun updatePassword(token: String, newPassword: String, callback: (Boolean) -> Unit) {
        val request = CheckPasswordRequest(newPassword)
        myPageApi.updateUserPassword(token, request)
            .enqueue(object : Callback<APIResponse> {
                override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                    callback(response.isSuccessful)
                }

                override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                    callback(false)
                }
            })
    }
}