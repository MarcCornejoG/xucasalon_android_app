package com.example.xucasalonapp.data.model

data class LoginResponse(
    val token: String,
    val idCliente: Int,
    val email: String? = null,
    val nombre: String? = null,
    val apellidos: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val fechaRegistro: String? = null,
    val firebaseUid: String? = null
)