package com.example.stocktracker.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserData? = null
)

data class UserData(
    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val username: String
)