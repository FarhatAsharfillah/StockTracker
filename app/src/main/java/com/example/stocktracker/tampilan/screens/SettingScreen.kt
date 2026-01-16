package com.example.stocktracker.tampilan.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocktracker.viewmodel.SettingsViewModel
import com.example.stocktracker.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val viewModel: SettingsViewModel = viewModel(factory = factory)

    // Ambil data threshold saat ini dari DataStore
    val currentThreshold by viewModel.threshold.observeAsState(initial = 5)

    // State untuk input text
    var inputThreshold by remember { mutableStateOf("") }

    // Saat data dari DataStore masuk/berubah, update isi textfield
    LaunchedEffect(currentThreshold) {
        inputThreshold = currentThreshold.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
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
            Text(
                text = "Batas Stok Rendah (Threshold)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Barang dengan stok di bawah angka ini akan ditandai sebagai 'Stok Menipis' di laporan.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = inputThreshold,
                onValueChange = { if (it.all { char -> char.isDigit() }) inputThreshold = it },
                label = { Text("Jumlah Minimum") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val newValue = inputThreshold.toIntOrNull()
                    if (newValue != null && newValue >= 0) {
                        viewModel.saveThreshold(newValue)
                        Toast.makeText(context, "Pengaturan disimpan!", Toast.LENGTH_SHORT).show()
                        navigateBack()
                    } else {
                        Toast.makeText(context, "Masukkan angka yang valid", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Simpan")
            }
        }
    }
}