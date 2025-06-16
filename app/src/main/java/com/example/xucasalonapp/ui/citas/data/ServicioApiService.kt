package com.example.xucasalonapp.ui.citas.data

import com.example.xucasalonapp.ui.citas.data.model.ServicioDTO
import retrofit2.http.GET

interface ServicioApiService {

    @GET("api/servicios")
    suspend fun obtenerTodosLosServicios(): List<ServicioDTO>
}