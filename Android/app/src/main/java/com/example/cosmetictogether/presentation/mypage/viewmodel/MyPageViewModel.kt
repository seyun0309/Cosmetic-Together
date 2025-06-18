package com.example.cosmetictogether.presentation.mypage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.MyPageRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.MyPageInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageViewModel : ViewModel() {
    private val _nickName = MutableLiveData<String>()
    val nickName: LiveData<String> get() = _nickName

    private val _profileUrl = MutableLiveData<String>()
    val profileUrl: LiveData<String> get() = _profileUrl

    private val _followingCount = MutableLiveData<Long>()
    val followingCount: LiveData<Long> get() = _followingCount

    private val _followerCount = MutableLiveData<Long>()
    val followerCount: LiveData<Long> get() = _followerCount

    private val myPageApi: MyPageRetrofitInterface =
        RetrofitClient.getInstance().create(MyPageRetrofitInterface::class.java)

    fun loadUserData(token: String) {

        myPageApi.getMyPageInfo(token).enqueue(object : Callback<MyPageInfoResponse> {
            override fun onResponse(call: Call<MyPageInfoResponse>, response: Response<MyPageInfoResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        _profileUrl.value = data.profileUrl
                        _nickName.value = data.nickName
                        _followerCount.value = data.followerCount
                        _followingCount.value = data.followingCount
                    }
                }
            }

            override fun onFailure(call: Call<MyPageInfoResponse>, t: Throwable) {
                // 실패 시 처리 로직 (예: 로그 출력)
            }
        })
    }
}
