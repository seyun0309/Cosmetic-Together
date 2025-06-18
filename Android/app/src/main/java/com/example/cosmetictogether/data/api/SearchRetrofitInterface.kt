package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.FormSummaryResponse
import com.example.cosmetictogether.data.model.PostRecentResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchRetrofitInterface {

    @GET("/api/v1/board")
    fun getPostByKeyword(
        @Query("keyword") keyword: String
    ): Call<List<PostRecentResponse>>

    @GET("/api/v1/form")
    fun getFormByKeyword(
        @Query("keyword") keyword: String
    ) : Call<List<FormSummaryResponse>>
}