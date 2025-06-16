package com.example.xucasalonapp.ui.citas.data.model

import java.math.BigDecimal

data class CitaServicio(
    val idCita: Int,
    val idServicio: Int,
    val servicio: Servicio,
    val precioAplicado: BigDecimal
)
