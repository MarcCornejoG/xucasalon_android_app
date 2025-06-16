package com.example.xucasalonapp.ui.login.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.ApiClient
import com.example.xucasalonapp.data.api.UsuarioService
import com.example.xucasalonapp.ui.usuario.data.UsuarioRepositoryImpl

class RegisterViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            val session = SessionManager(context)
            val apiClient = ApiClient.getInstance(session)
            val service = apiClient.createService(UsuarioService::class.java)
            val repo = UsuarioRepositoryImpl(service, session)
            return RegisterViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}