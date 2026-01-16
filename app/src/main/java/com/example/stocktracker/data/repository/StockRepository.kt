package com.example.stocktracker.data.repository

import com.example.stocktracker.data.local.dao.BarangDao
import com.example.stocktracker.data.local.dao.TransaksiDao
import com.example.stocktracker.data.local.entity.BarangEntity
import com.example.stocktracker.data.local.entity.TransaksiEntity
import kotlinx.coroutines.flow.Flow

class StockRepository private constructor(
    private val barangDao: BarangDao,
    private val transaksiDao: TransaksiDao
) {

    // --- BAGIAN BARANG ---

    fun getAllBarang(): Flow<List<BarangEntity>> = barangDao.getAllBarang()

    fun getLowStockBarang(threshold: Int): Flow<List<BarangEntity>> = barangDao.getLowStockBarang(threshold)

    fun getBarangById(id: Int): Flow<BarangEntity> = barangDao.getBarangById(id)

    suspend fun insertBarang(barang: BarangEntity) = barangDao.insertBarang(barang)

    suspend fun updateBarang(barang: BarangEntity) = barangDao.updateBarang(barang)

    suspend fun deleteBarang(barang: BarangEntity) = barangDao.deleteBarang(barang)

    // --- BAGIAN TRANSAKSI & STOK ---

    // 1. Ambil SEMUA Riwayat (Mungkin dipakai nanti untuk laporan)
    fun getRiwayatTransaksi(): Flow<List<TransaksiEntity>> = transaksiDao.getAllRiwayat()

    // 2. [TAMBAHAN BARU] Ambil Riwayat Per Barang (Untuk Halaman DetailBarangScreen)
    fun getRiwayatByBarangId(barangId: Int): Flow<List<TransaksiEntity>> {
        return transaksiDao.getRiwayatByBarangId(barangId)
    }

    // 3. Logic Transaksi: Simpan Transaksi + Update Stok di Barang
    // Ini Logic kamu yang sudah sangat bagus, kita pertahankan.
    suspend fun insertTransaksi(transaksi: TransaksiEntity, updateStokBarangId: Int, newStok: Int) {
        // Simpan log transaksi ke tabel riwayat
        transaksiDao.insertTransaksi(transaksi)
        // Update sisa stok ke tabel barang
        barangDao.updateStokBarang(updateStokBarangId, newStok)
    }

    companion object {
        @Volatile
        private var instance: StockRepository? = null

        fun getInstance(barangDao: BarangDao, transaksiDao: TransaksiDao): StockRepository =
            instance ?: synchronized(this) {
                instance ?: StockRepository(barangDao, transaksiDao).also { instance = it }
            }
    }
}