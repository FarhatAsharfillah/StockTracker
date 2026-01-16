package com.example.stocktracker.tampilan.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocktracker.data.local.entity.BarangEntity
import com.example.stocktracker.data.local.entity.TransaksiEntity
import com.example.stocktracker.viewmodel.BarangViewModel
import com.example.stocktracker.viewmodel.TransaksiViewModel
import com.example.stocktracker.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBarangScreen(
    barangId: Int,
    navigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onDeleteConfirmed: (BarangEntity) -> Unit
) {
    val context = LocalContext.current
    val factory = ViewModelFactory.getInstance(context)
    val barangViewModel: BarangViewModel = viewModel(factory = factory)
    val transaksiViewModel: TransaksiViewModel = viewModel(factory = factory)

    // --- STATE DATA ---
    val barang by barangViewModel.getBarangById(barangId).observeAsState()
    val listRiwayat by transaksiViewModel.getRiwayatByBarangId(barangId).observeAsState(emptyList())

    // --- STATE UI ---
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isTransaksiMasuk by remember { mutableStateOf(true) }
    var jumlahInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Barang") },
                // 1. BAGIAN KIRI: Tombol Kembali (ArrowBack)
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                // 2. BAGIAN KANAN: Tombol Edit & Hapus
                actions = {
                    // Tombol Edit
                    IconButton(onClick = { onNavigateToEdit(barangId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Barang")
                    }

                    // Tombol Hapus (Merah)
                    if (barang != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus Barang",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
            barang?.let { item ->
                // Info Barang
                InfoBarangCard(item)
                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Transaksi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tombol Masuk
                    Button(
                        onClick = {
                            isTransaksiMasuk = true
                            jumlahInput = ""
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Masuk")
                    }

                    // Tombol Keluar
                    Button(
                        onClick = {
                            isTransaksiMasuk = false
                            jumlahInput = ""
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Keluar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Riwayat
            Text(
                text = "Riwayat Transaksi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (listRiwayat.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Belum ada transaksi", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listRiwayat) { transaksi ->
                        RiwayatItem(transaksi)
                    }
                }
            }
        }
    }

    // --- DIALOG-DIALOG (Tidak berubah, copy paste bagian bawah ini saja jika perlu) ---

    // Dialog Hapus
    if (showDeleteDialog && barang != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Barang?") },
            text = {
                Text("Apakah Anda yakin ingin menghapus '${barang!!.namaBarang}'? Data riwayat transaksi juga akan terhapus permanen.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteConfirmed(barang!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Transaksi
    if (showDialog && barang != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(if (isTransaksiMasuk) "Tambah Stok" else "Keluarkan Stok")
            },
            text = {
                Column {
                    Text("Stok saat ini: ${barang!!.stok} ${barang!!.satuan}")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = jumlahInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) jumlahInput = it },
                        label = { Text("Jumlah") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val jumlah = jumlahInput.toIntOrNull() ?: 0
                        if (jumlah > 0) {
                            transaksiViewModel.simpanTransaksi(
                                barang = barang!!,
                                jumlah = jumlah,
                                isMasuk = isTransaksiMasuk,
                                onSuccess = {
                                    Toast.makeText(context, "Transaksi Berhasil", Toast.LENGTH_SHORT).show()
                                    showDialog = false
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "Masukkan jumlah valid", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


// --- KOMPONEN UI TAMBAHAN ---

@Composable
fun InfoBarangCard(barang: BarangEntity) {
    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Nama & Kategori
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = barang.namaBarang,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = barang.kategori,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                // Stok Besar di kanan
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${barang.stok}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = barang.satuan,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Info Harga
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Harga Beli", style = MaterialTheme.typography.labelSmall)
                    Text(formatRupiah.format(barang.hargaModal), fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Harga Jual", style = MaterialTheme.typography.labelSmall)
                    Text(
                        formatRupiah.format(barang.hargaJual),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun RiwayatItem(transaksi: TransaksiEntity) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))
    val tanggalStr = sdf.format(Date(transaksi.tanggal))

    val isMasuk = transaksi.jenis == "MASUK"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kiri: Jenis & Tanggal
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isMasuk) Icons.Default.Add else Icons.Default.Remove,
                        contentDescription = null,
                        tint = if (isMasuk) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isMasuk) "Stok Masuk" else "Stok Keluar",
                        fontWeight = FontWeight.Bold,
                        color = if (isMasuk) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
                Text(
                    text = tanggalStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 20.dp) // Indentasi biar sejajar teks atas
                )
            }

            // Kanan: Jumlah
            Text(
                text = "${transaksi.jumlah} ${transaksi.satuan}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}