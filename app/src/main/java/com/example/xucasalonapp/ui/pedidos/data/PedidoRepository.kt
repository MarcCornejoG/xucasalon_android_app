package com.example.xucasalonapp.ui.pedidos.data

import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.ui.pedidos.data.model.CrearPedidoDTO
import com.example.xucasalonapp.ui.pedidos.data.model.CreatePaymentIntentRequest
import com.example.xucasalonapp.ui.pedidos.data.model.PaymentConfirmationResponse
import com.example.xucasalonapp.ui.pedidos.data.model.PaymentIntentResponse
import com.example.xucasalonapp.ui.pedidos.data.model.Pedido
import com.example.xucasalonapp.ui.pedidos.data.model.PedidoCompleteDTO

interface PedidoRepository {
    suspend fun getPedidosPorCliente(idCliente: Int): Resource<List<Pedido>>
    suspend fun crearPedido(crearPedidoDTO: CrearPedidoDTO): Resource<PedidoCompleteDTO>

    suspend fun createPaymentIntent(request: CreatePaymentIntentRequest): Resource<PaymentIntentResponse>
    suspend fun confirmPayment(paymentIntentId: String): Resource<PaymentConfirmationResponse>
}