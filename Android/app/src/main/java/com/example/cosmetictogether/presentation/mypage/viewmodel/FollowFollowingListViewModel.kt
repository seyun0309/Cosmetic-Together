package com.example.cosmetictogether.presentation.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.FollowerListRequest
import com.example.cosmetictogether.data.model.FollowingListRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowFollowingListViewModel : ViewModel() {

    private val _followerList = MutableLiveData<List<FollowerListRequest>>()
    val followerList: LiveData<List<FollowerListRequest>> get() = _followerList

    private val _followingList = MutableLiveData<List<FollowingListRequest>>()
    val followingList: LiveData<List<FollowingListRequest>> get() = _followingList

    private val _loginMemberNickname = MutableLiveData<String>()
    val loginMemberNickname: LiveData<String> get() = _loginMemberNickname

    private val mypageApi = RetrofitClient.mypageApi

    fun setLoginMemberName(name: String) {
        _loginMemberNickname.value = name
    }

    fun loadFollowerList(token: String) {
        mypageApi.getFollowerList(token).enqueue(object : Callback<List<FollowerListRequest>> {
            override fun onResponse(
                call: Call<List<FollowerListRequest>>,
                response: Response<List<FollowerListRequest>>
            ) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    _followerList.value = list
                    if (list.isNotEmpty()) {
                        setLoginMemberName(list.first().loginMemberName)
                    }
                }
            }

            override fun onFailure(call: Call<List<FollowerListRequest>>, t: Throwable) {
                Log.e("ViewModel", "팔로워 목록 불러오기 실패", t)
            }
        })
    }

    fun loadFollowingList(token: String) {
        mypageApi.getFollowingList(token).enqueue(object : Callback<List<FollowingListRequest>> {
            override fun onResponse(
                call: Call<List<FollowingListRequest>>,
                response: Response<List<FollowingListRequest>>
            ) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    _followingList.value = list
                    if (list.isNotEmpty()) {
                        setLoginMemberName(list.first().loginMemberName)
                    }
                }
            }

            override fun onFailure(call: Call<List<FollowingListRequest>>, t: Throwable) {
                Log.e("ViewModel", "팔로잉 목록 불러오기 실패", t)
            }
        })
    }

    fun setFollowingList(updatedList: List<FollowingListRequest>) {
        _followingList.value = updatedList
    }
}