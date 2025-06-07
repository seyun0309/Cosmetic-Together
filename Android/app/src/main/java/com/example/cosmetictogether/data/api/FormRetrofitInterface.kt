package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.AccountResponse
import com.example.cosmetictogether.data.model.CreateFormRequest
import com.example.cosmetictogether.data.model.CreateFormResponse
import com.example.cosmetictogether.data.model.CreateOrderRequest
import com.example.cosmetictogether.data.model.CreateOrderResponse
import com.example.cosmetictogether.data.model.DetailFormResponse
import com.example.cosmetictogether.data.model.FormSummaryResponse
import com.example.cosmetictogether.data.model.MyFormResponse
import com.example.cosmetictogether.data.model.PostDeleteResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FormRetrofitInterface {
    @GET("/api/v1/form/recent")
    fun getFormRecent() : Call<List<FormSummaryResponse>>

    @GET("/api/v1/mypage/favorite-form")
    fun getFollowingForm(
        @Header("Authorization") token: String
    ) : Call<List<FormSummaryResponse>>

    @Multipart
    @POST("/api/v1/form")
    fun createForm(
        @Header("Authorization") token: String,
        @Part thumbnail: MultipartBody.Part,
        @Part("request") request: CreateFormRequest,
        @Part images: List<MultipartBody.Part>
    ): Call<CreateFormResponse>

    @GET("/api/v1/form/{formId}")
    fun getFormDetail(
        @Header("Authorization") token: String,
        @Path("formId") formId: Long
    ): Call<DetailFormResponse>

    @POST("/api/v1/order/{formId}")
    fun createOrder(
        @Path("formId") formId: Long,
        @Header("Authorization") token: String,
        @Body orderRequest: CreateOrderRequest
    ) : Call<CreateOrderResponse>

    @POST("api/v1/follow/{followingId}")
    fun followUser(
        @Path("followingId") followingId: Long,
        @Header("Authorization") token: String,
    ) : Call<APIResponse>


    @GET("/api/v1/order/my-form")
    fun getMyForm(@Header("Authorization") token: String): Call<List<MyFormResponse>>

    @GET("/api/v1/order/account/{orderId}")
    fun getAccount(@Path("orderId") orderId: Long,): Call<AccountResponse>

    @POST("/api/v1/favorite/{formId}")
    fun favoriteOrUnfavoriteForm(
        @Header("Authorization") token: String,
        @Path("formId") formId: Long
    ): Call<APIResponse>

    // 팔로우
    @POST("/api/v1/follow/form/{formId}")
    fun followOrUnfollow(
        @Header("Authorization") token: String,
        @Path("formId") formId: Long
    ): Call<APIResponse>

    @DELETE("api/v1/form/{formId}")
    fun postDelete(
        @Header("Authorization") token: String,
        @Path("formId") formId: Long
    ): Call<PostDeleteResponse>
}