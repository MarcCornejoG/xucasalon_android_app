package com.example.xucasalonapp.ui.citas.data

import com.example.xucasalonapp.ui.citas.data.model.ActualizarCitaRequest
import com.example.xucasalonapp.ui.citas.data.model.CitaDTO
import com.example.xucasalonapp.ui.citas.data.model.CrearCitaRequest
import com.example.xucasalonapp.ui.citas.data.model.HorarioDisponibleDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CitaApiService {
    @GET("api/citas/cliente/{idCliente}")
    suspend fun getCitasPorCliente(
        @Path("idCliente") idCliente: Int
    ): List<CitaDTO>

    @POST("api/citas")
    suspend fun crearCita(@Body cita: CrearCitaRequest): CitaDTO

    @PUT("api/citas/{id}")
    suspend fun actualizarCita(
        @Path("id") id: Int,
        @Body cita: ActualizarCitaRequest
    ): CitaDTO

    @GET("api/citas/disponibilidad/{idEmpleado}")
    suspend fun obtenerHorasDisponibles(
        @Path("idEmpleado") idEmpleado: Int,
        @Query("fecha") fecha: String
    ): List<HorarioDisponibleDTO>


    @PATCH("api/citas/{id}/cancelar")
    suspend fun cancelarCita(@Path("id") id: Int)
}