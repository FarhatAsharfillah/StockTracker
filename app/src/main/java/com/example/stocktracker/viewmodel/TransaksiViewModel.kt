package com.example.stocktracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocktracker.data.local.entity.BarangEntity
import com.example.stocktracker.data.local.entity.TransaksiEntity
import com.example.stocktracker.data.repository.StockRepository
import kotlinx.coroutines.launch

class TransaksiViewModel(private val repository: StockRepository) : ViewModel() {

    // 1. Ambil SEMUA riwayat transaksi (Untuk halaman Laporan Global nanti)
    val allRiwayatTransaksi: LiveData<List<TransaksiEntity>> = repository.getRiwayatTransaksi().asLiveData()

    // 2. [BARU] Ambil riwayat KHUSUS satu barang (Untuk DetailBarangScreen)
    // Sesuai REQ-10
    fun getRiwayatByBarangId(barangId: Int): LiveData<List<TransaksiEntity>> {
        return repository.getRiwayatByBarangId(barangId).asLiveData()
    }

    // 3. Fungsi Eksekusi Transaksi
    // Kita buat fungsi pintar ini supaya UI tinggal kirim data mentah, biar ViewModel yang mikir hitungannya.
    fun simpanTransaksi(
        barang: BarangEntity,
        jumlah: Int,
        isMasuk: Boolean,
        onSuccess: () -> Unit, // Callback opsional biar UI tau kapan selesai
        onError: (String) -> Unit // Callback error (misal stok negatif)
    ) {
        viewModelScope.launch {
            // A. Hitung Stok Baru
            val stokBaru = if (isMasuk) {
                barang.stok + jumlah
            } else {
                barang.stok - jumlah
            }

            // B. Validasi (REQ-8: Stok tidak boleh negatif)
            if (stokBaru < 0) {
                onError("Stok tidak mencukupi!")
                return@launch
            }

            // C. Siapkan Object Transaksi (Sesuai Entity kamu)
            val transaksiBaru = TransaksiEntity(
                barangId = barang.id,
                namaBarang = barang.namaBarang, // Snapshot Nama
                satuan = barang.satuan,         // Snapshot Satuan
                jumlah = jumlah,
                jenis = if (isMasuk) "MASUK" else "KELUAR",
                tanggal = System.currentTimeMillis()
            )

            // D. Kirim ke Repository (Simpan Transaksi + Update Stok Barang)
            repository.insertTransaksi(transaksiBaru, barang.id, stokBaru)

            onSuccess()
        }
    }
}