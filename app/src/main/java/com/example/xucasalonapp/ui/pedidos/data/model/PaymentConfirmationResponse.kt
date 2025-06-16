package com.example.xucasalonapp.ui.pedidos.data.model

data class PaymentConfirmationResponse(
    val status: String,
    val paymentIntentId: String,
    val idPedido: String?
)