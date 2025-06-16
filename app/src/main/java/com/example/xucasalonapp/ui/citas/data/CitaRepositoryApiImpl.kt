package com.example.xucasalonapp.ui.citas.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.ApiClient
import com.example.xucasalonapp.ui.citas.data.model.ActualizarCitaRequest
import com.example.xucasalonapp.ui.citas.data.model.Cita
import com.example.xucasalonapp.ui.citas.data.model.CrearCitaRequest
import com.example.xucasalonapp.ui.citas.data.model.toDomain
import java.time.LocalDate
import java.time.LocalTime

class CitaRepositoryApiImpl(
    sessionManager: SessionManager
) : CitaRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    private val api = ApiClient
        .getInstance(sessionManager)
        .createService(CitaApiService::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getCitasPorCliente(idCliente: Int): List<Cita> {
        try {
            val citasDto = api.getCitasPorCliente(idCliente)
            Log.d("CitaRepository", "DTOs recibidos: ${citasDto.size}")
            val citasDomain = citasDto.map { it.toDomain() }
            Log.d("CitaRepository", "Citas convertidas: ${citasDomain.size}")
            return citasDomain
        } catch (e: Exception) {
            Log.e("CitaRepository", "Error en getCitasPorCliente", e)
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun crearCita(cita: Cita): Cita {

        val request = CrearCitaRequest(
            idCliente = cita.clienteId,
            idEmpleado = cita.empleadoId,
            fecha = cita.fecha.toString(),
            horaInicio = cita.horaInicio.toString(),
            horaFin = cita.horaFin.toString(),
            estado = cita.estado.name,
            notas = cita.notas,
            serviciosIds = cita.citaServicios.map { it.idServicio }
        )

        val citaCreada = api.crearCita(request)
        return citaCreada.toDomain()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun actualizarCita(cita: Cita): Cita {
        val request = ActualizarCitaRequest(
            idCita = cita.idCita,
            idCliente = cita.clienteId,
            idEmpleado = cita.empleadoId,
            fecha = cita.fecha.toString(),
            horaInicio = cita.horaInicio.toString(),
            horaFin = cita.horaFin.toString(),
            estado = cita.estado.name,
            notas = cita.notas,
            serviciosIds = cita.citaServicios.map { it.idServicio }
        )

        val citaActualizada = api.actualizarCita(cita.idCita, request)
        return citaActualizada.toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun obtenerHorasDisponibles(
        idEmpleado: Int,
        fecha: LocalDate,
        duracionRequerida: Int
    ): List<LocalTime> {
        try {
            val fechaString = fecha.toString()
            val rangosDisponibles = api.obtenerHorasDisponibles(idEmpleado, fechaString)

            Log.d("CitaRepository", "Rangos disponibles recibidos: ${rangosDisponibles.size}")

            val horasDisponibles = mutableListOf<LocalTime>()

            rangosDisponibles.forEach { rango ->
                try {
                    val horaInicio = LocalTime.parse(rango.horaInicio)
                    val horaFin = LocalTime.parse(rango.horaFin)

                    Log.d("CitaRepository", "Procesando rango: $horaInicio - $horaFin")

                    var horaActual = horaInicio
                    while (horaActual.plusMinutes(duracionRequerida.toLong()).isBefore(horaFin) ||
                        horaActual.plusMinutes(duracionRequerida.toLong()).equals(horaFin)) {
                        horasDisponibles.add(horaActual)
                        horaActual = horaActual.plusMinutes(30)
                    }

                } catch (e: Exception) {
                    Log.e("CitaRepository", "Error parsing rango: ${rango.horaInicio} - ${rango.horaFin}", e)
                }
            }

            val horasOrdenadas = horasDisponibles.distinct().sorted()
            Log.d("CitaRepository", "Horas disponibles generadas: ${horasOrdenadas.size}")

            return horasOrdenadas

        } catch (e: Exception) {
            Log.e("CitaRepository", "Error en obtenerHorasDisponibles", e)
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun cancelarCita(idCita: Int) {
        try {
            api.cancelarCita(idCita)
            Log.d("CitaRepository", "Cita $idCita cancelada exitosamente")
        } catch (e: Exception) {
            Log.e("CitaRepository", "Error cancelando cita $idCita", e)
            throw e
        }
    }
}


