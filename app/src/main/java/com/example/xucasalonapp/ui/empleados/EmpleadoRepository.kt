package com.example.xucasalonapp.ui.empleados

import com.example.xucasalonapp.ui.empleados.data.Empleado

interface EmpleadoRepository {
    suspend fun obtenerTodosLosEmpleados(): List<Empleado>
    suspend fun obtenerEmpleadoPorId(id: Int): Empleado
}