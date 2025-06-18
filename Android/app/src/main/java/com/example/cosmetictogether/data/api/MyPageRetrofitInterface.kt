package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.AccountInfoResponse
import com.example.cosmetictogether.data.model.CheckPasswordRequest
import com.example.cosmetictogether.data.model.FollowerListRequest
import com.example.cosmetictogether.data.model.FollowingListRequest
import com.example.cosmetictogether.data.model.MyPageInfoResponse
import com.example.cosmetictogether.data.model.UpdateAddressRequest
import com.example.cosmetictogether.data.model.UpdateNicknameRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MyPageRetrofitInterface {
    @GET("/api/v1/mypage")
    fun getMyPageInfo(
        @Header("Authorization") token: String
    ): Call<MyPageInfoResponse>

    @GET("/api/v1/mypage/info")
    fun getUserInfo(
        @Header("Authorization") token: String
    ): Call<AccountInfoResponse>

    @POST("/api/v1/mypage/address")
    fun updateUserAddress(
        @Header("Authorization") token: String,
        @Body updateAddressRequest: UpdateAddressRequest
    ): Call<APIResponse>

    @POST("/api/v1/mypage/check")
    fun verifyPassword(
        @Header("Authorization") token: String,
        @Body checkPasswordRequest: CheckPasswordRequest
    ): Call<APIResponse>

    @POST("/api/v1/mypage/password")
    fun updateUserPassword(
        @Header("Authorization") token: String,
        @Body checkPasswordRequest: CheckPasswordRequest
    ): Call<APIResponse>

    @POST("/api/v1/nickname")
    fun checkNicknameDuplicate(
        @Body updateNicknameRequest: UpdateNicknameRequest
    ): Call <APIResponse>

    @POST("/api/v1/mypage/nickname")
    fun updateUserNickname(
        @Header("Authorization") token: String,
        @Body updateNicknameRequest: UpdateNicknameRequest
    ): Call <APIResponse>

    @GET("/api/v1/mypage/followers")
    fun getFollowerList(
        @Header("Authorization") token: String,
    ): Call<List<FollowerListRequest>>

    @GET("/api/v1/mypage/followings")
    fun getFollowingList(
        @Header("Authorization") token: String,
    ): Call<List<FollowingListRequest>>

    @POST("/api/v1/follow/mypage/{memberId}")
    fun followOrUnfollowByMemberId(
        @Header("Authorization") token: String,
        @Path("memberId") memberId: Long
    ): Call<APIResponse>

    @Multipart
    @POST("/api/v1/mypage/profileImg")
    fun updateProfileImg(
        @Header("Authorization") token: String,
        @Part images: MultipartBody.Part,
    ): Call<APIResponse>
}