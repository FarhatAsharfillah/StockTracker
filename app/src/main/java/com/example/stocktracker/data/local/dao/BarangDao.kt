package com.example.stocktracker.data.local.dao

import androidx.room.*
import com.example.stocktracker.data.local.entity.BarangEntity
import com.example.stocktracker.data.local.entity.TransaksiEntity // <-- PENTING: Tambahkan Import ini
import kotlinx.coroutines.flow.Flow

@Dao
interface BarangDao {
    // --- BAGIAN BARANG (Sudah ada sebelumnya) ---

    // Ambil semua barang urut abjad
    @Query("SELECT * FROM barang ORDER BY namaBarang ASC")
    fun getAllBarang(): Flow<List<BarangEntity>>

    // Ambil satu barang detail
    @Query("SELECT * FROM barang WHERE id = :id")
    fun getBarangById(id: Int): Flow<BarangEntity>

    // Cari barang
    @Query("SELECT * FROM barang WHERE namaBarang LIKE '%' || :query || '%'")
    fun searchBarang(query: String): Flow<List<BarangEntity>>

    // Low Stock Report
    @Query("SELECT * FROM barang WHERE stok <= :threshold ORDER BY stok ASC")
    fun getLowStockBarang(threshold: Int): Flow<List<BarangEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarang(barang: BarangEntity)

    @Update
    suspend fun updateBarang(barang: BarangEntity)

    @Delete
    suspend fun deleteBarang(barang: BarangEntity)

    // Update stok saja
    @Query("UPDATE barang SET stok = :newStok WHERE id = :id")
    suspend fun updateStokBarang(id: Int, newStok: Int)


    // --- BAGIAN TRANSAKSI (TAMBAHAN BARU) ---

    // 1. Simpan riwayat transaksi (REQ-9)
    @Insert
    suspend fun insertTransaksi(transaksi: TransaksiEntity)

    // 2. Ambil daftar riwayat berdasarkan ID Barang (REQ-10)
    // Diurutkan dari tanggal paling baru (DESC) biar yg baru muncul di atas
    @Query("SELECT * FROM transaksi_stok WHERE barangId = :barangId ORDER BY tanggal DESC")
    fun getRiwayatByBarangId(barangId: Int): Flow<List<TransaksiEntity>>
}