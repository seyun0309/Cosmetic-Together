package com.example.cosmetictogether.presentation.post.adapter

import com.example.cosmetictogether.data.api.CommentRetrofitInterface
import com.example.cosmetictogether.data.api.PostRetrofitInterface
import com.example.cosmetictogether.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class PostRepository(
    private val apiService: PostRetrofitInterface,
) {

    // 글 작성 (텍스트 + 이미지)
    suspend fun postWrite(
        token: String,
        text: RequestBody,
        images: List<MultipartBody.Part>
    ): Response<PostWriteResponse> {
        return apiService.postWrite(token, images, text).awaitResponse()
    }

    // 글 수정 (텍스트 + 이미지)
    suspend fun postEdit(
        boardId: Long,
        token: String,
        text: RequestBody,
        images: List<MultipartBody.Part>
    ): Response<PostWriteResponse> {
        return apiService.postEdit(token, boardId, images, text).awaitResponse()
    }

    // 최근 게시글 조회
    suspend fun getRecentPosts(): List<PostRecentResponse> {
        val response = apiService.postRecent().awaitResponse()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("게시글 목록을 불러오지 못했습니다.")
        }
    }

    // 나의 게시글 조회
    suspend fun getMyPosts(token: String): List<PostRecentResponse> {
        val response = apiService.getMyPosts(token).awaitResponse()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("게시글 목록을 불러오지 못했습니다.")
        }
    }

    // 팔로잉 게시글 조회
    suspend fun getFollowingPosts(token: String): List<PostRecentResponse> {
        val response = apiService.getFollowingPost(token).awaitResponse()
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