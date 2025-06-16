package com.example.xucasalonapp.ui.productos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.productos.data.ProductApiService
import com.example.xucasalonapp.ui.productos.data.ProductoRepositoryApiImpl
import com.example.xucasalonapp.ui.productos.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoDetailViewModel(
    sessionManager: SessionManager
) : ViewModel() {
    private val repo: ProductApiService=
        ProductoRepositoryApiImpl(sessionManager)

    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> = _producto

    fun loadById(id: Int) {
        viewModelScope.launch {
            _producto.value = repo.getAllProducts().find { it.idProducto == id }
        }
    }
}
