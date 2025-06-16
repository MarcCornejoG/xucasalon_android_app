package com.example.xucasalonapp.ui.citas.data.model

data class CrearCitaRequest(
    val idCliente: Int,
    val idEmpleado: Int,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: String = "PENDIENTE",
    val notas: String?,
    val serviciosIds: List<Int>
)
