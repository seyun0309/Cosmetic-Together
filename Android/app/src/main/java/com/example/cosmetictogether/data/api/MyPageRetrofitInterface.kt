package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.MyPageInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MyPageRetrofitInterface {
    @GET("/api/v1/mypage")
    fun getMyPageInfo(
        @Header("Authorization") token: String
    ): Call<MyPageInfoResponse>
}