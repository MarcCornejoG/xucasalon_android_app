package com.example.xucasalonapp.ui.citas.data

import com.example.xucasalonapp.ui.citas.data.model.Cita
import java.time.LocalDate
import java.time.LocalTime

interface CitaRepository {

   suspend fun getCitasPorCliente(idCliente: Int): List<Cita>
   suspend fun crearCita(cita: Cita): Cita
   suspend fun actualizarCita(cita: Cita): Cita

   suspend fun obtenerHorasDisponibles(
      idEmpleado: Int,
      fecha: LocalDate,
      duracionRequerida: Int = 30
   ): List<LocalTime>


   suspend fun cancelarCita(idCita: Int)

}