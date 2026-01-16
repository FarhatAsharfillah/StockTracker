package com.example.stocktracker.tampilan.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stocktracker.R
import com.example.stocktracker.viewmodel.AuthViewModel
import com.example.stocktracker.viewmodel.ViewModelFactory

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context)
    )

    val isLoading by authViewModel.isLoading.observeAsState(initial = false)
    val loginResult by authViewModel.loginResult.observeAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginResult) {
        loginResult?.onSuccess {
            Toast.makeText(context, "Login Berhasil: ${it.message}", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
        loginResult?.onFailure {
            Toast.makeText(context, "Login Gagal: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Tambahkan scroll state agar layar bisa digeser saat keyboard muncul
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp) // Padding kiri-kanan standar
            .verticalScroll(scrollState), // FITUR PENTING: Agar bisa discroll
        verticalArrangement = Arrangement.Center, // Konten tetap di tengah secara vertikal
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- LOGO ---
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Aplikasi",
            modifier = Modifier
                .size(120.dp) // UKURAN IDEAL: Jangan terlalu besar
                .padding(bottom = 8.dp) // Jarak dekat ke teks StockTracker
        )

        // --- JUDUL ---
        Text(
            text = "StockTracker",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp)) // Jarak ke Input Field

        // --- INPUTS ---
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- TOMBOL ---
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        authViewModel.login(username, password)
                    } else {
                        Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // Tinggi tombol biar gagah
            ) {
                Text("Masuk")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Belum punya akun? Daftar di sini")
        }
    }
}