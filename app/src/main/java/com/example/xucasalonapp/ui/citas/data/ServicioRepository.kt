package com.example.xucasalonapp.ui.citas.data

import com.example.xucasalonapp.ui.citas.data.model.Servicio

interface ServicioRepository {
    suspend fun obtenerTodosLosServicios(): List<Servicio>
}