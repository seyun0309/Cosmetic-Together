package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName

data class PostRecentResponse( // 최근 게시글
    @SerializedName("boardId") val boardId: Long, // 확인 필요
    @SerializedName("writerNickName") val writerNickName: String,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("boardUrl") val boardUrl: List<String>,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("postTime") val postTime: String,
    @SerializedName("commentCount") val commentCount: Int,
)

data class PostWriteResponse( // 게시글 작성 응답
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)

data class PostAfterEditResponse(
    @SerializedName("description") val description: String,
    @SerializedName("boardUrl") val boardUrl: List<String>
)

data class PostDetailResponse( // 게시글 상세 조회 응답
    @SerializedName("boardId") val boardId: Long,
    @SerializedName("writerNickName") val writerNickName: String,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("boardUrl") val boardUrl: List<String>,
    @SerializedName("postTime") val postTime: String,
    @SerializedName("following") val following: Boolean,
    @SerializedName("comments") val comments: List<Comment>,
    @SerializedName("liked") val isLiked: Boolean
)

data class CommentRequest(
    @SerializedName("boardId") val boardId: Long,
    @SerializedName("comment") val comment: String
)

data class Comment(
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("commenter") val commenter: String,
    @SerializedName("comment") val comment: String,
    @SerializedName("commentAt") val commentAt: String
)

data class PostDeleteResponse( // 게시글 삭제 응답
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)

data class PostEditRequest( // 게시글 수정 요청
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("request") val request: RequestDetails
)

data class RequestDetails(
    @SerializedName("description") val description: String
)

data class PostEditResponse( // 게시글 수정 응답
    @SerializedName("images") val images: List<String>,
    @SerializedName("request") val request: RequestDetails
)




