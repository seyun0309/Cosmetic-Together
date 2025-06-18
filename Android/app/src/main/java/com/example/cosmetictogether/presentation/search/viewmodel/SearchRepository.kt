package com.example.cosmetictogether.presentation.search.viewmodel

import com.example.cosmetictogether.data.api.SearchRetrofitInterface
import com.example.cosmetictogether.data.model.FormSummaryResponse
import com.example.cosmetictogether.data.model.PostRecentResponse
import retrofit2.awaitResponse

class SearchRepository(
    private val apiSearch: SearchRetrofitInterface
){
   suspend fun getPostByKeyword(keyword: String): List<PostRecentResponse> {
       val response = apiSearch.getPostByKeyword(keyword).awaitResponse()
       if (response.isSuccessful) {
           return response.body() ?: emptyList()
       } else {
           throw Exception("게시글 목록을 불러오지 못했습니다.")
       }
    }

    suspend fun getFormByKeyword(keyword: String): List<FormSummaryResponse> {
        val response = apiSearch.getFormByKeyword(keyword).awaitResponse()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("폼 목록을 불러오지 못했습니다.")
        }
    }
}