package com.example.xucasalonapp.ui.pedidos.data.model


data class PaymentIntentResponse(
    val clientSecret: String,
    val paymentIntentId: String
)
