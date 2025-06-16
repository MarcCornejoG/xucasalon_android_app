package com.example.xucasalonapp.ui.empleados.data

import java.time.LocalDate

data class EmpleadoDTO(
val idEmpleado: Int,
val email: String,
val nombre: String,
val apellido1: String,
val apellido2: String?,
val telefono: String,
val esAdmin: Boolean,
val fechaContratacion: LocalDate
)