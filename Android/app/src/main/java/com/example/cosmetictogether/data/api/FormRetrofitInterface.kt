package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.CreateFormRequest
import com.example.cosmetictogether.data.model.CreateFormResponse
import com.example.cosmetictogether.data.model.FormSummaryResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
}