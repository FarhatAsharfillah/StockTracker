package com.example.stocktracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaksi_stok",
    // --- TAMBAHAN INI PENTING ---
    // Fungsinya: Kalau 'Barang' induk dihapus, riwayatnya ikut terhapus otomatis (biar gak error/nyampah)
    foreignKeys = [
        ForeignKey(
            entity = BarangEntity::class,
            parentColumns = ["id"],
            childColumns = ["barangId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
    // ----------------------------
)
data class TransaksiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val barangId: Int,
    val namaBarang: String,
    val satuan: String,
    val jumlah: Int,
    val jenis: String, // "MASUK" atau "KELUAR"
    val tanggal: Long
)