package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName

data class MyPageInfoResponse(
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("followingCount") val followingCount: Long,
    @SerializedName("followerCount") val followerCount: Long
)

data class AccountInfoResponse(
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("address") val address: String,
)

data class UpdateAddressRequest(
    @SerializedName("address") val address: String,
)

data class CheckPasswordRequest(
    @SerializedName("password") val password: String,
)

data class UpdatePasswordRequest(
    @SerializedName("password") val password: String,
)

data class UpdateNicknameRequest(
    @SerializedName("nickName") val nickname: String,
)

data class FollowerListRequest(
    @SerializedName("loginMemberName") val loginMemberName: String,
    @SerializedName("followerMemberId") val followingMemberId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileUrl") val profileUrl: String,
)

data class FollowingListRequest(
    @SerializedName("loginMemberName") val loginMemberName: String,
    @SerializedName("followingMemberId") val followingMemberId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("following") val following: Boolean
)