package com.example.xucasalonapp.ui.pedidos.data.model

import java.math.BigDecimal
import java.util.Date


data class PedidoDTO(
    val idPedido: Int,
    val fechaPedido: Date,
    val estado: String,
    val total: BigDecimal,
    val metodoPago: String,
    val fechaPago: Date?,
    val productos: List<Any>
)

fun PedidoDTO.toDomain(): Pedido = Pedido(
    id = idPedido,
    fecha = fechaPedido,
    estado = estado,
    total = total,
    metodoPago = metodoPago,
    fechaPago = fechaPago
)