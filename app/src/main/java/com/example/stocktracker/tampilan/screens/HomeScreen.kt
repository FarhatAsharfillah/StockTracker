package com.example.stocktracker.tampilan.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Pastikan import ini ada
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocktracker.tampilan.components.BarangItem
import com.example.stocktracker.viewmodel.BarangViewModel
import com.example.stocktracker.viewmodel.SettingsViewModel
import com.example.stocktracker.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToTransaction: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val barangViewModel: BarangViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

    val listBarang by barangViewModel.allBarang.observeAsState(initial = emptyList())
    val threshold by settingsViewModel.threshold.observeAsState(initial = 5)

    // State Notifikasi
    val snackbarHostState = remember { SnackbarHostState() }

    // Logika Deteksi Stok
    LaunchedEffect(listBarang, threshold) {
        val lowStockCount = listBarang.count { it.stok <= threshold }
        if (lowStockCount > 0) {
            val result = snackbarHostState.showSnackbar(
                message = "Perhatian! Ada $lowStockCount barang stok menipis.",
                actionLabel = "LIHAT",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onNavigateToTransaction()
            }
        }
    }

    Scaffold(
        // parameter snackbarHost DIHAPUS dari sini

        topBar = {
            TopAppBar(
                title = { Text("Stock Tracker") },
                actions = {
                    IconButton(onClick = onNavigateToTransaction) {
                        Icon(Icons.Default.Assessment, contentDescription = "Laporan")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = onNavigateToSettings,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Pengaturan")
                }

                FloatingActionButton(onClick = onNavigateToAdd) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Barang")
                }
            }
        }
    ) { innerPadding ->
        // Kita bungkus konten dengan Box agar bisa menumpuk UI (Stack)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Padding agar tidak tertutup TopBar
        ) {
            // 1. KONTEN UTAMA (LIST BARANG)
            if (listBarang.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data barang. Yuk tambah!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listBarang) { barang ->
                        BarangItem(
                            barang = barang,
                            threshold = threshold,
                            onClick = { onNavigateToDetail(barang.id) }
                        )
                    }
                }
            }

            // 2. NOTIFIKASI (SNACKBAR) DI POSISI ATAS
            // Kita taruh di sini agar muncul DI ATAS list barang (Z-Index paling tinggi)
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter) // Posisi di Tengah Atas
                    .padding(top = 16.dp)       // Beri jarak sedikit dari Header
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White,
                    actionColor = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
            }
        }
    }
}