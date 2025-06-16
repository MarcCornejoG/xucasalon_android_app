package com.example.xucasalonapp.ui.pedidos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.pedidos.data.PedidoRepository


class CarritoViewModelFactory(
    private val sessionManager: SessionManager,
    private val pedidoRepository: PedidoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarritoViewModel(sessionManager, pedidoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}