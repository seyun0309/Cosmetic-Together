package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.CommentRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CommentRetrofitInterface {
    @POST("/api/v1/comment")
    fun postComment(
        @Header("Authorization") token: String,
        @Body request: CommentRequest
    ): Call<APIResponse>
}