package com.example.xucasalonapp.ui.pedidos.data.model

import com.example.xucasalonapp.ui.usuario.data.model.ClienteDTO
import java.math.BigDecimal
import java.util.Date

data class PedidoCompleteDTO(
    val idPedido: Int,
    val cliente: ClienteDTO,
    val fechaPedido: Date,
    val estado: String,
    val total: BigDecimal,
    val metodoPago: String?,
    val fechaPago: Date?,
    val productos: List<PedidoProductoDTO>
)
fun PedidoCompleteDTO.toDomain(): Pedido = Pedido(
    id = idPedido,
    fecha = fechaPedido,
    estado = estado,
    total = total,
    metodoPago = metodoPago ?: "",
    fechaPago = fechaPago
)