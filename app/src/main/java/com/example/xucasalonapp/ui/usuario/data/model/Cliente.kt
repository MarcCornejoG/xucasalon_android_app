package com.example.xucasalonapp.ui.usuario.data.model

data class Cliente(
    val idCliente: Int,
    val email: String,
    val nombre: String,
    val apellidos: String,
    val telefono: String,
    val direccion: String?,
    val fechaRegistro: String,
    val firebaseUid: String? = null
)