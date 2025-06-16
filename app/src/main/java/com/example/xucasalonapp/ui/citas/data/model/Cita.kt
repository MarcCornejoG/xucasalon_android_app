package com.example.xucasalonapp.ui.citas.data.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Cita(
    val idCita: Int,
    val clienteId: Int,
    val empleadoId: Int,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val estado: EstadoCita,
    val notas: String?,
    val fechaCreacion: LocalDateTime?,
    val citaServicios: List<CitaServicio>
)
