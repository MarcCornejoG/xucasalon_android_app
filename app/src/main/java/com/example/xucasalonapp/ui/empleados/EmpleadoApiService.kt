package com.example.xucasalonapp.ui.empleados

import com.example.xucasalonapp.ui.empleados.data.EmpleadoDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface EmpleadoApiService {
    @GET("api/empleados")
    suspend fun getAllEmpleados(): List<EmpleadoDTO>

    @GET("api/empleados/{id}")
    suspend fun getEmpleadoById(@Path("id") id: Int): EmpleadoDTO
}