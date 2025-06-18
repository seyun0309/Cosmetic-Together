package com.example.cosmetictogether.presentation.post.adapter

import com.example.cosmetictogether.data.api.CommentRetrofitInterface
import com.example.cosmetictogether.data.api.PostRetrofitInterface
import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.CommentRequest
import com.example.cosmetictogether.data.model.PostDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class PostDetailRepository(
    private val apiService: PostRetrofitInterface,
    private val commentApi: CommentRetrofitInterface
) {
    // 게시글 상세 조회
    fun getPostDetail(token: String, boardId: String, callback: (PostDetailResponse?, Throwable?) -> Unit) {
        val call = apiService.postDetail(token, boardId)
        call.enqueue(object : Callback<PostDetailResponse> {
            override fun onResponse(call: Call<PostDetailResponse>, response: Response<PostDetailResponse>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, Throwable("상세 정보를 불러오는 데 실패했습니다: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<PostDetailResponse>, t: Throwable) {
                callback(null, t)
            }
        })
    }

    // 댓글 작성
    fun postComment(token: String, request: CommentRequest, onSuccess: () -> Unit, onFailure: () -> Unit) {
        commentApi.postComment(token, request).enqueue(object : Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                if (response.isSuccessful) onSuccess()
                else onFailure()
            }

            override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                onFailure()
            }
        })
    }


    // 게시글 삭제
    suspend fun deletePost(token: String, boardId: Long): Boolean {
        return try {
            val response = apiService.postDelete(token, boardId).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }


    // 좋아요 클릭
    suspend fun toggleLike(boardId: Long, token: String): Boolean {
        val response = apiService.likeOrUnlikePost(token, boardId).awaitResponse()
        return response.isSuccessful
    }

    suspend fun toggleFollow(boardId: Long, token: String): Boolean {
        val response = apiService.followOrUnfollow(token, boardId).awaitResponse()
        return response.isSuccessful
    }
}