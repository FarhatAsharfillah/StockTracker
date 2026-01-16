package com.example.stocktracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stocktracker.data.local.dao.BarangDao
import com.example.stocktracker.data.local.dao.TransaksiDao
import com.example.stocktracker.data.local.entity.BarangEntity
import com.example.stocktracker.data.local.entity.TransaksiEntity

// Pastikan entities berisi KEDUA class ini
@Database(
    entities = [BarangEntity::class, TransaksiEntity::class],
    version = 2, // Versi naik jadi 2
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {

    // GUNAKAN INI (Bukan StockDao)
    abstract fun barangDao(): BarangDao

    // DAN INI (Yang baru)
    abstract fun transaksiDao(): TransaksiDao

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                )
                    .fallbackToDestructiveMigration() // PENTING: Tambahkan ini agar tidak crash saat upgrade versi
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}