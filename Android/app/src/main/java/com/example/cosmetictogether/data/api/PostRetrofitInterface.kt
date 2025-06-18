package com.example.cosmetictogether.data.api

import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.data.model.PostWriteResponse
import com.example.cosmetictogether.data.model.PostAfterEditResponse
import com.example.cosmetictogether.data.model.PostDeleteResponse
import com.example.cosmetictogether.data.model.PostDetailResponse
import com.example.cosmetictogether.data.model.PostRecentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PostRetrofitInterface {
    @Multipart
    @POST("/api/v1/board") // 게시글 작성
    fun postWrite(
        @Header("Authorization") token: String,
        @Part images: List<MultipartBody.Part>,
        @Part("request") request: RequestBody
    ): Call<PostWriteResponse>

    // 최근 게시글 목록 조회
    @GET("/api/v1/board/recent")
    fun postRecent(): Call<List<PostRecentResponse>>

    // 팔로잉 게시글 목록 조회
    @GET("/api/v1/board/following")
    fun getFollowingPost(
        @Header("Authorization") token: String
    ): Call<List<PostRecentResponse>>

    @GET("/api/v1/board/info/{boardId}") // 게시글 수정 전 기존 내용 불러오기
    fun postAfterEdit(@Path("boardId") boardId: String): Call<PostAfterEditResponse>

    @GET("/api/v1/board/{boardId}") // 게시글 상세 조회
    fun postDetail(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: String
    ): Call<PostDetailResponse>

    @DELETE("/api/v1/board/{boardId}") // 게시글 삭제
    fun postDelete(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long
    ): Call<PostDeleteResponse>

    @Multipart
    @POST("/api/v1/board/{boardId}") // 게시글 수정
    fun postEdit(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long,
        @Part images: List<MultipartBody.Part>,
        @Part("request") request: RequestBody
    ): Call<PostWriteResponse>

    // 좋아요 게시글 목록 조회
    @GET("/api/v1/mypage/liked-board")
    fun getLikePost(
        @Header("Authorization") token: String
    ): Call<List<PostRecentResponse>>

    // 나의 게시글 조회
    @GET("/api/v1/mypage/posts")
    fun getMyPosts(
        @Header("Authorization") token: String
    ): Call<List<PostRecentResponse>>

    // 좋아요
    @POST("/api/v1/like/{boardId}")
    fun likeOrUnlikePost(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long
    ): Call<APIResponse>

    // 팔로우
    @POST("/api/v1/follow/board/{boardId}")
    fun followOrUnfollow(
        @Header("Authorization") token: String,
        @Path("boardId") boardId: Long
    ): Call<APIResponse>
}
