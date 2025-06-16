package com.example.xucasalonapp.ui.pedidos.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.ui.pedidos.data.PedidoRepository
import com.example.xucasalonapp.ui.pedidos.data.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PedidosViewModel(
private val repository: PedidoRepository,
private val idCliente: Int
) : ViewModel() {

    private val _pedidos = MutableStateFlow<Resource<List<Pedido>>>(Resource.Loading)
    val pedidos: StateFlow<Resource<List<Pedido>>> = _pedidos

    init {
        cargarPedidos()
    }

    private fun cargarPedidos() {
        viewModelScope.launch {
            _pedidos.value = Resource.Loading
            _pedidos.value = repository.getPedidosPorCliente(idCliente)
        }
    }
}

class PedidosViewModelFactory(
    private val repository: PedidoRepository,
    private val idCliente: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidosViewModel::class.java)) {
            return PedidosViewModel(repository, idCliente) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}