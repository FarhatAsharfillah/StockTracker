package com.example.stocktracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocktracker.data.local.entity.BarangEntity
import com.example.stocktracker.data.repository.StockRepository
import kotlinx.coroutines.launch

class BarangViewModel(private val repository: StockRepository) : ViewModel() {

    val allBarang: LiveData<List<BarangEntity>> = repository.getAllBarang().asLiveData()

    fun insertBarang(barang: BarangEntity) {
        viewModelScope.launch { repository.insertBarang(barang) }
    }

    fun updateBarang(barang: BarangEntity) {
        viewModelScope.launch { repository.updateBarang(barang) }
    }

    fun deleteBarang(barang: BarangEntity) {
        viewModelScope.launch { repository.deleteBarang(barang) }
    }

    // PERBAIKAN: Pastikan parameter di sini 'id: Int', BUKAN Long.
    fun getBarangById(id: Int): LiveData<BarangEntity> {
        return repository.getBarangById(id).asLiveData()
    }
}