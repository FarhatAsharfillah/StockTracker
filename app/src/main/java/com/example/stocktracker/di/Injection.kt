package com.example.stocktracker.di

import android.content.Context
import com.example.stocktracker.data.local.StockDatabase
import com.example.stocktracker.data.remote.api.ApiClient // <-- Pakai ini, bukan ApiConfig
import com.example.stocktracker.data.repository.AuthRepository
import com.example.stocktracker.data.repository.StockRepository

object Injection {

    // KEMBALIKAN KE LOGIKA AWAL (Sesuai ApiClient kamu)
    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiClient.instance
        return AuthRepository.getInstance(apiService)
    }

    // UPDATE BAGIAN INI SAJA (Untuk support Transaksi)
    fun provideStockRepository(context: Context): StockRepository {
        val database = StockDatabase.getDatabase(context)

        val barangDao = database.barangDao()
        val transaksiDao = database.transaksiDao() // Ambil DAO baru

        // Kirim dua DAO ke Repository
        return StockRepository.getInstance(barangDao, transaksiDao)
    }
}