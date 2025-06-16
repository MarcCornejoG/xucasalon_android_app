package com.example.xucasalonapp.ui.empleados.data

import java.time.LocalDate

data class Empleado(
    val idEmpleado: Int,
    val email: String,
    val nombre: String,
    val apellido1: String,
    val apellido2: String?,
    val telefono: String,
    val esAdmin: Boolean,
    val fechaContratacion: LocalDate
){
    val nombreCompleto: String
        get() = if (apellido2 != null) {
            "$nombre $apellido1 $apellido2"
        } else {
            "$nombre $apellido1"
        }
}

fun EmpleadoDTO.toDomain(): Empleado {
    return Empleado(
        idEmpleado = this.idEmpleado,
        email = this.email,
        nombre = this.nombre,
        apellido1 = this.apellido1,
        apellido2 = this.apellido2,
        telefono = this.telefono,
        esAdmin = this.esAdmin,
        fechaContratacion = this.fechaContratacion
    )
}