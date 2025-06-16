package com.example.xucasalonapp.ui.productos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.xucasalonapp.data.SessionManager

class ProductoDetailViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoDetailViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}