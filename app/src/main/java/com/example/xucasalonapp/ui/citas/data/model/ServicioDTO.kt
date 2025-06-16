package com.example.xucasalonapp.ui.citas.data.model

import java.math.BigDecimal

data class ServicioDTO(
    val idServicio: Int,
    val nombre: String,
    val descripcion: String?,
    val tipo: TipoServicio,
    val precio: BigDecimal,
    val duracion: Int
)
