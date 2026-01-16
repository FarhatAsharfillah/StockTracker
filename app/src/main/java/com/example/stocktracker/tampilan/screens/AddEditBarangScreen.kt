package com.example.stocktracker.tampilan.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocktracker.data.local.entity.BarangEntity
import com.example.stocktracker.viewmodel.BarangViewModel
import com.example.stocktracker.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBarangScreen(
    itemId: Long,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: BarangViewModel = viewModel(factory = ViewModelFactory.getInstance(context))

    // --- DATA PILIHAN DROPDOWN ---
    val listKategori = listOf(
        "Bahan Pokok", "Makanan Ringan", "Minuman", "Produk Kebersihan",
        "Kebutuhan Rumah Tangga", "Produk Perawatan Pribadi", "Makanan Instan",
        "Bumbu dan Penyedap", "Produk Beku / Olahan", "Lain-lain"
    )
    val listSatuan = listOf("Pcs", "Kg", "Karton", "Liter", "Pack", "Botol")

    // --- STATE FORM ---
    var namaBarang by remember { mutableStateOf("") }
    var stok by remember { mutableStateOf("") }
    var hargaBeli by remember { mutableStateOf("") } // Harga Modal
    var hargaJual by remember { mutableStateOf("") } // Harga Jual (Manual)

    // Default value
    var kategori by remember { mutableStateOf(listKategori.first()) }
    var satuan by remember { mutableStateOf(listSatuan.first()) }

    val isEditMode = itemId != -1L

    // --- LOAD DATA (MODE EDIT) ---
    val barangTerpilih by viewModel.getBarangById(itemId.toInt()).observeAsState()

    LaunchedEffect(barangTerpilih) {
        if (isEditMode) {
            barangTerpilih?.let {
                namaBarang = it.namaBarang
                stok = it.stok.toString()
                hargaBeli = it.hargaModal.toString()
                hargaJual = it.hargaJual.toString()

                kategori = if (listKategori.contains(it.kategori)) it.kategori else "Lain-lain"
                satuan = if (listSatuan.contains(it.satuan)) it.satuan else listSatuan.first()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Barang" else "Tambah Barang") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
                // Actions (Tombol Hapus) SUDAH DIHAPUS DARI SINI (karena dipindah ke Detail)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. INPUT NAMA
            OutlinedTextField(
                value = namaBarang,
                onValueChange = { namaBarang = it },
                label = { Text("Nama Barang") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. INPUT KATEGORI (DROPDOWN)
            StockDropdown(
                label = "Kategori",
                options = listKategori,
                selectedOption = kategori,
                onOptionSelected = { kategori = it }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // 3. INPUT STOK (DENGAN LOGIKA READ-ONLY SAAT EDIT)
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = stok,
                        onValueChange = {
                            // Hanya bisa diketik jika BUKAN mode edit
                            if (!isEditMode && it.all { char -> char.isDigit() }) stok = it
                        },
                        label = { Text("Stok") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        // --- LOGIKA UTAMA: Read Only saat Edit ---
                        readOnly = isEditMode,
                        colors = OutlinedTextFieldDefaults.colors(
                            // Ubah warna background jadi agak abu-abu kalau readOnly
                            unfocusedContainerColor = if (isEditMode) Color.LightGray.copy(alpha = 0.2f) else Color.Transparent,
                            focusedContainerColor = if (isEditMode) Color.LightGray.copy(alpha = 0.2f) else Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Pesan helper kecil di bawah field stok
                    if (isEditMode) {
                        Text(
                            text = "Edit stok via menu Detail",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }

                // 4. INPUT SATUAN (DROPDOWN)
                Box(modifier = Modifier.weight(1f)) {
                    StockDropdown(
                        label = "Satuan",
                        options = listSatuan,
                        selectedOption = satuan,
                        onOptionSelected = { satuan = it }
                    )
                }
            }

            // 5. INPUT HARGA MODAL (BELI)
            OutlinedTextField(
                value = hargaBeli,
                onValueChange = { if (it.all { char -> char.isDigit() }) hargaBeli = it },
                label = { Text("Harga Modal (Beli)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 6. INPUT HARGA JUAL (MANUAL)
            OutlinedTextField(
                value = hargaJual,
                onValueChange = { if (it.all { char -> char.isDigit() }) hargaJual = it },
                label = { Text("Harga Jual") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 7. TOMBOL SIMPAN
            Button(
                onClick = {
                    if (namaBarang.isNotEmpty() && stok.isNotEmpty() && hargaBeli.isNotEmpty() && hargaJual.isNotEmpty()) {

                        val modalValue = hargaBeli.toIntOrNull() ?: 0
                        val jualValue = hargaJual.toIntOrNull() ?: 0
                        val stokValue = stok.toIntOrNull() ?: 0

                        val barangBaru = BarangEntity(
                            id = if (isEditMode) itemId.toInt() else 0,
                            namaBarang = namaBarang,
                            kategori = kategori,
                            stok = stokValue,
                            satuan = satuan,
                            hargaModal = modalValue,
                            hargaJual = jualValue,
                            imagePath = null
                        )

                        if (isEditMode) {
                            viewModel.updateBarang(barangBaru)
                            Toast.makeText(context, "Berhasil Diupdate", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.insertBarang(barangBaru)
                            Toast.makeText(context, "Berhasil Ditambah", Toast.LENGTH_SHORT).show()
                        }
                        navigateBack()
                    } else {
                        Toast.makeText(context, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Simpan")
            }
        }
    }
}

// --- KOMPONEN REUSABLE UNTUK DROPDOWN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = { },
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}