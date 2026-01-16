package com.example.stocktracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stocktracker.data.local.UserPreferences
import com.example.stocktracker.data.repository.AuthRepository
import com.example.stocktracker.data.repository.StockRepository
import com.example.stocktracker.di.Injection

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(
    private val authRepository: AuthRepository,
    private val stockRepository: StockRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Jika UI minta AuthViewModel, bikinkan pakai AuthRepository
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        // ... (di dalam method create) ...

        // Jika UI minta BarangViewModel
        if (modelClass.isAssignableFrom(BarangViewModel::class.java)) {
            return BarangViewModel(stockRepository) as T
        }

        // --- TAMBAHAN BARU: TransaksiViewModel ---
        if (modelClass.isAssignableFrom(TransaksiViewModel::class.java)) {
            return TransaksiViewModel(stockRepository) as T
        }

        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(userPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideAuthRepository(),
                    Injection.provideStockRepository(context),
                    UserPreferences.getInstance(context)
                ).also { instance = it }
            }
    }
}