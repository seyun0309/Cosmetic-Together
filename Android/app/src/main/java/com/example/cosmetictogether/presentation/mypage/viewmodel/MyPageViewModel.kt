package com.example.cosmetictogether.presentation.mypage.viewmodel

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.MyPageRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.MyPageInfoResponse
import com.example.cosmetictogether.presentation.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageViewModel : ViewModel() {
    private val _nickName = MutableLiveData<String>()
    val nickName: LiveData<String> get() = _nickName

    private val _profileUrl = MutableLiveData<String>()
    val profileUrl: LiveData<String> get() = _profileUrl

    private val myPageApi: MyPageRetrofitInterface =
        RetrofitClient.getInstance().create(MyPageRetrofitInterface::class.java)

    fun loadUserData(token: String) {
        val authToken = "Bearer $token"

        myPageApi.getMyPageInfo(authToken).enqueue(object : Callback<MyPageInfoResponse> {
            override fun onResponse(call: Call<MyPageInfoResponse>, response: Response<MyPageInfoResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _profileUrl.value = data.profileUrl
                        _nickName.value = data.nickName
                    }
                }
            }

            override fun onFailure(call: Call<MyPageInfoResponse>, t: Throwable) {
                // 실패 시 처리 로직 (예: 로그 출력)
            }
        })
    }
}
