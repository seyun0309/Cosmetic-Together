package com.example.cosmetictogether.presentation.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.MyPageRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.AccountInfoResponse
import com.example.cosmetictogether.data.model.UpdateAddressRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountInfoViewModel : ViewModel() {

    private val _accountInfo = MutableLiveData<AccountInfoResponse>()
    val accountInfo: LiveData<AccountInfoResponse> = _accountInfo

    private val myPageApi: MyPageRetrofitInterface =
        RetrofitClient.getInstance().create(MyPageRetrofitInterface::class.java)

    fun loadAccountInfo(token: String) {
        myPageApi.getUserInfo(token).enqueue(object : Callback<AccountInfoResponse> {
            override fun onResponse(
                call: Call<AccountInfoResponse>,
                response: Response<AccountInfoResponse>
            ) {
                if (response.isSuccessful) {
                    _accountInfo.value = response.body()
                } else {
                    Log.e("AccountInfo", "서버 응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AccountInfoResponse>, t: Throwable) {
                Log.e("AccountInfo", "API 호출 실패: ${t.message}")
            }
        })
    }

    fun updateAddress(token: String, newAddress: String) {
        val request = UpdateAddressRequest(newAddress)
        myPageApi.updateUserAddress(token, request)
            .enqueue(object : Callback<APIResponse> {
                override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                    if (response.isSuccessful) {
                        // 성공 처리 (예: 토스트 띄우기 등)
                    }
                }

                override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                    // 실패 처리
                }
            })
    }
}