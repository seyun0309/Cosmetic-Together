package com.example.cosmetictogether.presentation.post.adapter

import com.example.cosmetictogether.data.api.PostRetrofitInterface
import com.example.cosmetictogether.data.model.PostRecentResponse
import retrofit2.Response
import retrofit2.awaitResponse

class LikePostRepository(
    private val apiService: PostRetrofitInterface,
) {
    // 좋아요 게시글 목록 조회
    suspend fun getLikePost(token: String): List<PostRecentResponse> {
        val response = apiService.getLikePost(token).awaitResponse()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("게시글 목록을 불러오지 못했습니다.")
        }
    }

    // 공통 예외 처리 래퍼
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 본문이 없습니다."))
            } else {
                val error = response.errorBody()?.string()
                Result.failure(Exception("에러 ${response.code()}: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}