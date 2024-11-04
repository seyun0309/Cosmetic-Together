package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName

data class MyPageInfoResponse(
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("address") val address: String,
    @SerializedName("profile_url") val profile_url: String,
    @SerializedName("status_msg") val status_msg: String,
    @SerializedName("background_url") val background_url: String,
)