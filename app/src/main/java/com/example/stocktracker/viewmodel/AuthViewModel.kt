package com.example.stocktracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stocktracker.data.remote.response.LoginResponse
import com.example.stocktracker.data.remote.response.RegisterResponse
import com.example.stocktracker.data.repository.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // LiveData untuk memantau status Login
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    // LiveData untuk memantau status Register
    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    // LiveData untuk Loading (Menampilkan Progress Bar)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, pass: String) {
        _isLoading.value = true
        val client = repository.login(username, pass)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status) {
                        _loginResult.value = Result.success(body)
                    } else {
                        _loginResult.value = Result.failure(Exception(body.message))
                    }
                } else {
                    _loginResult.value = Result.failure(Exception("Terjadi kesalahan: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _loginResult.value = Result.failure(Exception("Gagal koneksi: ${t.message}"))
            }
        })
    }

    fun register(username: String, pass: String, passConf: String) {
        _isLoading.value = true
        val client = repository.register(username, pass, passConf)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status) {
                        _registerResult.value = Result.success(body)
                    } else {
                        _registerResult.value = Result.failure(Exception(body.message))
                    }
                } else {
                    _registerResult.value = Result.failure(Exception("Terjadi kesalahan: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _registerResult.value = Result.failure(Exception("Gagal koneksi: ${t.message}"))
            }
        })
    }
}