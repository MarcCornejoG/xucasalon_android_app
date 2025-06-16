package com.example.xucasalonapp.ui.citas.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.ApiClient
import com.example.xucasalonapp.ui.citas.data.model.Servicio
import com.example.xucasalonapp.ui.citas.data.model.toDomain

class ServicioRepositoryImpl(
    private val sessionManager: SessionManager
) : ServicioRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val api = ApiClient
        .getInstance(sessionManager)
        .createService(ServicioApiService::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun obtenerTodosLosServicios(): List<Servicio> {
        return api.obtenerTodosLosServicios().map { it.toDomain() }
    }
}