package com.example.stocktracker.tampilan.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.stocktracker.data.local.entity.BarangEntity
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BarangItem(
    barang: BarangEntity,
    threshold: Int,
    onClick: () -> Unit
) {
    // Format Rupiah sederhana
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

    val isLowStock = barang.stok <= threshold
    val stokColor = if (isLowStock) Color.Red else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Kolom Kiri: Info Barang & Harga
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = barang.namaBarang,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = barang.kategori,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tampilan Harga
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Harga Modal (Kecil)
                    Text(
                        text = "Beli: ${formatRupiah.format(barang.hargaModal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // Harga Jual (Lebih Menonjol)
                    Text(
                        text = "Jual: ${formatRupiah.format(barang.hargaJual)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Kolom Kanan: Stok
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${barang.stok}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = stokColor
                )
                Text(
                    text = barang.satuan,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                if (isLowStock) {
                    Text(
                        text = "Stok Tipis!",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}