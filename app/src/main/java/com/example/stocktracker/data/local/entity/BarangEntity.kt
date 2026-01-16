package com.example.stocktracker.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "barang")
data class BarangEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaBarang: String,
    val kategori: String,
    val satuan: String,
    val hargaModal: Int, // Menggunakan Int
    val hargaJual: Int,  // Menggunakan Int
    val stok: Int,
    val imagePath: String? = null
) : Parcelable