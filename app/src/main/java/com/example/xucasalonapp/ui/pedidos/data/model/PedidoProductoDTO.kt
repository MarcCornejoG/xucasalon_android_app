package com.example.xucasalonapp.ui.pedidos.data.model

import java.math.BigDecimal

data class PedidoProductoDTO(
    val idProducto: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: BigDecimal,
    val subtotal: BigDecimal
)