package com.example.xucasalonapp.ui.citas.data.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class CitaDTO(
    val idCita: Int,
    val idCliente: Int,
    val idEmpleado: Int,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val estado: EstadoCita,
    val notas: String?,
    val fechaCreacion: LocalDateTime?,
    val serviciosIds: List<Int>,
    val servicios: List<ServicioDTO>
)
