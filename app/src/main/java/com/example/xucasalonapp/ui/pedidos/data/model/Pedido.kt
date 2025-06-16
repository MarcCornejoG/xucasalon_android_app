package com.example.xucasalonapp.ui.pedidos.data.model

import java.math.BigDecimal
import java.util.Date

data class Pedido(
    val id: Int,
    val fecha: Date,
    val estado: String,
    val total: BigDecimal,
    val metodoPago: String?,
    val fechaPago: Date?
)
