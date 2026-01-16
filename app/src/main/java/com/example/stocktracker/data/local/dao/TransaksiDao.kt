package com.example.stocktracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stocktracker.data.local.entity.TransaksiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaksiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaksi(transaksi: TransaksiEntity)

    // Ambil SEMUA riwayat (Untuk Laporan Global)
    @Query("SELECT * FROM transaksi_stok ORDER BY tanggal DESC")
    fun getAllRiwayat(): Flow<List<TransaksiEntity>>

    // --- TAMBAHAN PENTING (REQ-10) ---
    // Ambil riwayat KHUSUS satu barang (Untuk Detail Barang)
    @Query("SELECT * FROM transaksi_stok WHERE barangId = :barangId ORDER BY tanggal DESC")
    fun getRiwayatByBarangId(barangId: Int): Flow<List<TransaksiEntity>>
}