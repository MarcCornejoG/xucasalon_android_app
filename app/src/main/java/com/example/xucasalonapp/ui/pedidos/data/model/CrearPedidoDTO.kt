package com.example.xucasalonapp.ui.pedidos.data.model

data class CrearPedidoDTO(
    var idCliente: Int? = null,
    var productosYCantidades: Map<Int, Int> = emptyMap()
)
