package com.example.stocktracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocktracker.data.local.UserPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: UserPreferences) : ViewModel() {

    // Ambil data threshold sebagai LiveData agar mudah diobserve di UI
    val threshold: LiveData<Int> = pref.thresholdFlow.asLiveData()

    fun saveThreshold(newThreshold: Int) {
        viewModelScope.launch {
            pref.saveThreshold(newThreshold)
        }
    }
}