package com.example.stocktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// Pastikan import di bawah ini sesuai nama package kamu
import com.example.stocktracker.tampilan.Screen
import com.example.stocktracker.tampilan.screens.AddEditBarangScreen
import com.example.stocktracker.tampilan.screens.HomeScreen
import com.example.stocktracker.tampilan.screens.LoginScreen
import com.example.stocktracker.tampilan.screens.DetailBarangScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.stocktracker.tampilan.screens.LaporanScreen
import com.example.stocktracker.tampilan.screens.SettingsScreen
import com.example.stocktracker.viewmodel.BarangViewModel
import com.example.stocktracker.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StockTrackerApp()
                }
            }
        }
    }
}

@Composable
fun StockTrackerApp() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // 1. LOGIN
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { /* Nanti dibuat */ }
                )
            }

            // 2. HOME
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAdd = {
                        navController.navigate(Screen.AddEdit.createRoute())
                    },
                    onNavigateToDetail = { barangId ->
                        navController.navigate("detail_barang/$barangId")
                    },
                    onNavigateToTransaction = {
                        navController.navigate("laporan")
                    },
                    // --- PARAMETER BARU ---
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            composable("laporan") {
                LaporanScreen(
                    navigateBack = { navController.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    navigateBack = { navController.popBackStack() }
                )
            }

            // 3. ADD / EDIT BARANG
            composable(
                route = Screen.AddEdit.route, // Pastikan di Screen.kt routenya: "add_edit_barang?itemId={itemId}"
                arguments = listOf(navArgument("itemId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getLong("itemId") ?: -1L

                AddEditBarangScreen(
                    itemId = itemId,
                    navigateBack = { navController.popBackStack() }
                )
            }

            // 4. TRANSACTION & REPORT (Placeholder biar gak error)
            // --- TAMBAHKAN INI: Rute ke Detail Barang ---
            // 4. DETAIL BARANG (Update blok ini)
            composable(
                route = "detail_barang/{barangId}",
                arguments = listOf(navArgument("barangId") { type = NavType.IntType })
            ) { backStackEntry ->
                val barangId = backStackEntry.arguments?.getInt("barangId") ?: -1

                // --- TAMBAHAN: Dapatkan ViewModel di sini untuk aksi hapus ---
                val context = LocalContext.current
                val barangViewModel: BarangViewModel = viewModel(
                    factory = ViewModelFactory.getInstance(context)
                )

                DetailBarangScreen(
                    barangId = barangId,
                    navigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.AddEdit.createRoute(id.toLong()))
                    },
                    // --- TAMBAHAN BARU: Implementasi Callback Hapus ---
                    onDeleteConfirmed = { barangKurban ->
                        // Panggil fungsi delete di ViewModel
                        barangViewModel.deleteBarang(barangKurban)
                        // Kembali ke halaman Home setelah dihapus
                        navController.popBackStack()
                        // Opsional: Tampilkan Toast di sini jika mau
                    }
                )
            }
        }
    }
}