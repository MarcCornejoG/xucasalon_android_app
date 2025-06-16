package com.example.xucasalonapp.ui.pedidos.data.model

import java.math.BigDecimal

data class PedidoProducto(
    val idPedido: Int,
    val idProducto: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: BigDecimal
)
