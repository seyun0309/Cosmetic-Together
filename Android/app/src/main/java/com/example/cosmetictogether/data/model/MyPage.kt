package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName

data class MyPageInfoResponse(
    @SerializedName("profileUrl") val profileUrl: String,
    @SerializedName("nickName") val nickName: String
)