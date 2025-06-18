package com.example.cosmetictogether.presentation.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.MyPageRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.UpdateNicknameRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NicknameViewModel: ViewModel() {

    private val myPageApi: MyPageRetrofitInterface =
        RetrofitClient.getInstance().create(MyPageRetrofitInterface::class.java)

    fun checkNicknameDuplicate(nickname: String, callback: (isAvailable: Boolean, message: String) -> Unit) {
        val request = UpdateNicknameRequest(nickname)
        myPageApi.checkNicknameDuplicate(request).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                val message = response.body()?.message ?: "알 수 없는 오류"
                when (response.code()) {
                    200 -> callback(true, message)
                    409 -> callback(false, message)
                    else -> callback(false, "서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                callback(false, "네트워크 오류: ${t.message}")
            }
        })
    }

    fun updateNickname(token: String, nickname: String, callback: (Boolean) -> Unit) {
        val request = UpdateNicknameRequest(nickname)
        Log.d("닉네임 확인", "닉네임 : $request")
        myPageApi.updateUserNickname(token, request).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                callback(response.isSuccessful)
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                callback(false)
            }
        })
    }
}