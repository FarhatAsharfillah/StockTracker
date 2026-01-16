package com.example.stocktracker.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // PENTING:
    // Jika pakai Emulator Android: Gunakan "http://10.0.2.2/folder_xampp_kamu/"
    // Jika pakai HP Fisik (Debugging): Gunakan IP Laptop (misal "http://192.168.1.X/folder_xampp_kamu/")
    // Pastikan Laptop dan HP di Wifi yang sama.

    private const val BASE_URL = "http://10.0.2.2/stocktracker_api/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}