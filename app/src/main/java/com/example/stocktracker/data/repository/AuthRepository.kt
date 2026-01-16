package com.example.stocktracker.data.repository

import com.example.stocktracker.data.remote.api.ApiService
import com.example.stocktracker.data.remote.response.LoginResponse
import com.example.stocktracker.data.remote.response.RegisterResponse
import retrofit2.Call

class AuthRepository private constructor(private val apiService: ApiService) {

    // Fungsi Login
    fun login(username: String, pass: String): Call<LoginResponse> {
        return apiService.loginUser(username, pass)
    }

    // Fungsi Register
    fun register(username: String, pass: String, passConf: String): Call<RegisterResponse> {
        return apiService.registerUser(username, pass, passConf)
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(apiService: ApiService): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService).also { instance = it }
            }
    }
}