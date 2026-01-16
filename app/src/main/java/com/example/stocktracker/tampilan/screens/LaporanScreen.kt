package com.example.stocktracker.tampilan.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocktracker.data.local.entity.BarangEntity
import com.example.stocktracker.viewmodel.BarangViewModel
import com.example.stocktracker.viewmodel.SettingsViewModel
import com.example.stocktracker.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val barangViewModel: BarangViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

    // Ambil Data
    val listBarang by barangViewModel.allBarang.observeAsState(emptyList())
    val threshold by settingsViewModel.threshold.observeAsState(5)

    // Filter State
    var showOnlyLowStock by remember { mutableStateOf(true) }

    // Logic Filter
    val filteredList = if (showOnlyLowStock) {
        listBarang.filter { it.stok <= threshold }
    } else {
        listBarang
    }

    // Hitung Total Aset Keseluruhan
    val totalAset = listBarang.sumOf { it.stok * it.hargaModal.toLong() }
    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Aset & Stok") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 1. KARTU TOTAL KESELURUHAN (HEADER)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Nilai Aset Gudang", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = formatRupiah.format(totalAset),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Akumulasi dari ${listBarang.size} jenis barang",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. FILTER TABS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = showOnlyLowStock,
                    onClick = { showOnlyLowStock = true },
                    label = { Text("Stok Menipis") },
                    leadingIcon = { if (showOnlyLowStock) Icon(Icons.Default.Warning, null) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFE0B2),
                        selectedLabelColor = Color(0xFFE65100),
                        selectedLeadingIconColor = Color(0xFFE65100)
                    )
                )
                FilterChip(
                    selected = !showOnlyLowStock,
                    onClick = { showOnlyLowStock = false },
                    label = { Text("Semua Barang") },
                    leadingIcon = { if (!showOnlyLowStock) Icon(Icons.Default.CheckCircle, null) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. LIST ITEMS
            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Tidak ada data.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredList) { barang ->
                        LaporanItem(barang, threshold, formatRupiah)
                    }
                }
            }
        }
    }
}

// --- KOMPONEN ITEM LAPORAN (YANG DIPERBAIKI) ---
@Composable
fun LaporanItem(barang: BarangEntity, threshold: Int, numberFormat: NumberFormat) {
    val isLow = barang.stok <= threshold

    // REQ-13: Hitung Nilai Aset per Barang
    val nilaiAset = barang.stok * barang.hargaModal

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Kasih border merah kalau stok tipis
        border = if (isLow) androidx.compose.foundation.BorderStroke(1.dp, Color.Red) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Baris 1: Nama Barang & Nilai Aset Besar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = barang.namaBarang,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Menampilkan Nilai Aset (Paling Menonjol)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Nilai Aset",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = numberFormat.format(nilaiAset),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // Baris 2: Detail Hitungan (Stok x Harga)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kiri: Rumus Hitungan
                Column {
                    Text(
                        text = "Kalkulasi:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "${barang.stok} ${barang.satuan} x ${numberFormat.format(barang.hargaModal)} = ${numberFormat.format(nilaiAset)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold // Sedikit tebal biar jelas
                    )
                }

                // Kanan: Status Stok
                if (isLow) {
                    Surface(
                        color = Color.Red.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Stok Tipis (${barang.stok})",
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Stok Aman",
                        color = Color(0xFF4CAF50), // Hijau
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}