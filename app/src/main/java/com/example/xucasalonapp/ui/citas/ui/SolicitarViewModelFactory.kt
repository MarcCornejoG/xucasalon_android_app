package com.example.xucasalonapp.ui.citas.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.citas.data.CitaRepositoryApiImpl
import com.example.xucasalonapp.ui.citas.data.ServicioRepositoryImpl
import com.example.xucasalonapp.ui.empleados.EmpleadoRepositoryApiImpl

@RequiresApi(Build.VERSION_CODES.O)
class SolicitarCitaViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolicitarCitaViewModel::class.java)) {
            val citaRepository = CitaRepositoryApiImpl(sessionManager)
            val servicioRepository = ServicioRepositoryImpl(sessionManager)
            val empleadoRepository = EmpleadoRepositoryApiImpl(sessionManager)
            @Suppress("UNCHECKED_CAST")
            return SolicitarCitaViewModel(
                citaRepository,
                servicioRepository,
                empleadoRepository,
                sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


