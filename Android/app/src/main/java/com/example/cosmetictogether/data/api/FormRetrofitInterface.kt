package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.CreateFormRequest
import com.example.cosmetictogether.data.model.CreateFormResponse
import com.example.cosmetictogether.data.model.CreateOrderRequest
import com.example.cosmetictogether.data.model.DetailFormResponse
import com.example.cosmetictogether.data.model.FormSummaryResponse
import com.example.cosmetictogether.data.model.MyFormResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FormRetrofitInterface {
    @GET("/api/v1/form/recent")
    fun getFormRecent() : Call<List<FormSummaryResponse>>

    @GET("/api/v1/form/following")
    fun getFollowingForm() : Call<List<FormSummaryResponse>>

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
        @Path("formId") formId: Long,
        @Header("Authorization") token: String,) : Call<DetailFormResponse>

    @POST("/api/v1/order/{formId}")
    fun createOrder(
        @Path("formId") formId: Long,
        @Header("Authorization") token: String,
        @Body orderRequest: CreateOrderRequest
    ) : Call<APIResponse>

    @POST("api/v1/follow/{followingId}")
    fun followUser(
        @Path("followingId") followingId: Long,
        @Header("Authorization") token: String,
    ) : Call<APIResponse>


    @GET("/api/v1/order/my-form")
    fun getMyForm(@Header("Authorization") token: String): Call<List<MyFormResponse>>
}