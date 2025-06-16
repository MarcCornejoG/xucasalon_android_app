package com.example.xucasalonapp.ui.usuario.data.model

data class ClienteDTO(
    var idCliente: Int? = null,
    var email: String? = null,
    var nombre: String? = null,
    var apellidos: String? = null,
    var telefono: String? = null,
    var direccion: String? = null,
    var password: String? = null,
    var fechaRegistro: String? = null ,
    var googleId: String? = null

)