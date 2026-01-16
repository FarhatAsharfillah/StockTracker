package com.example.stocktracker.data.remote.api

import com.example.stocktracker.data.remote.response.LoginResponse
import com.example.stocktracker.data.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun registerUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConf: String
    ): Call<RegisterResponse>
}