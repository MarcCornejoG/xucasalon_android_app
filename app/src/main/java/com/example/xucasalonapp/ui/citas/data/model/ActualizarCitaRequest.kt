package com.example.xucasalonapp.ui.citas.data.model

data class ActualizarCitaRequest(
    val idCita: Int,
    val idCliente: Int,
    val idEmpleado: Int,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: String,
    val notas: String?,
    val serviciosIds: List<Int>
)
