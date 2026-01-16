package com.example.stocktracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property buat bikin datastore (hanya 1 instance)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences private constructor(private val context: Context) {

    // Kunci untuk menyimpan threshold
    private val THRESHOLD_KEY = intPreferencesKey("threshold_stok")

    // Fungsi membaca threshold (Flow agar live update)
    // Default kita kasih 5 kalau belum disetting
    val thresholdFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[THRESHOLD_KEY] ?: 5
        }

    // Fungsi menyimpan threshold
    suspend fun saveThreshold(threshold: Int) {
        context.dataStore.edit { preferences ->
            preferences[THRESHOLD_KEY] = threshold
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }
}