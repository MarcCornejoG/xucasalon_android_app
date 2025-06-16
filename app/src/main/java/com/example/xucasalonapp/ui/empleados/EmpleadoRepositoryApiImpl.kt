package com.example.xucasalonapp.ui.empleados

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.ApiClient
import com.example.xucasalonapp.ui.empleados.data.Empleado
import com.example.xucasalonapp.ui.empleados.data.toDomain


class EmpleadoRepositoryApiImpl(
    private val sessionManager: SessionManager
) : EmpleadoRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val api = ApiClient
        .getInstance(sessionManager)
        .createService(EmpleadoApiService::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun obtenerTodosLosEmpleados(): List<Empleado> {
        try {
            val empleadosDto = api.getAllEmpleados()
            return empleadosDto.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("EmpleadoRepository", "Error al obtener empleados", e)
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun obtenerEmpleadoPorId(id: Int): Empleado {
        try {
            val empleadoDto = api.getEmpleadoById(id)
            return empleadoDto.toDomain()
        } catch (e: Exception) {
            Log.e("EmpleadoRepository", "Error al obtener empleado por ID", e)
            throw e
        }
    }
}