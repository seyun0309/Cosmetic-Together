package com.example.cosmetictogether.data.model

import com.google.gson.annotations.SerializedName

data class FollowUser (
    @SerializedName("followingId") val followingId: Long
)