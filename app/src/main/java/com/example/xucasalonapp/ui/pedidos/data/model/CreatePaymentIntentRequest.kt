package com.example.xucasalonapp.ui.pedidos.data.model

data class CreatePaymentIntentRequest(
    val amount: Long,
    val currency: String = "eur",
    val idPedido: Int
)