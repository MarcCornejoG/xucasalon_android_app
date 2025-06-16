package com.example.xucasalonapp.ui.citas.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.citas.data.CitaRepository
import com.example.xucasalonapp.ui.citas.data.ServicioRepository
import com.example.xucasalonapp.ui.citas.data.ServicioRepositoryImpl
import com.example.xucasalonapp.ui.citas.data.model.Cita
import com.example.xucasalonapp.ui.citas.data.model.CitaServicio
import com.example.xucasalonapp.ui.citas.data.model.CrearCitaRequest
import com.example.xucasalonapp.ui.citas.data.model.EstadoCita
import com.example.xucasalonapp.ui.citas.data.model.Servicio
import com.example.xucasalonapp.ui.citas.data.model.TipoServicio
import com.example.xucasalonapp.ui.empleados.EmpleadoRepository
import com.example.xucasalonapp.ui.empleados.data.Empleado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId




@RequiresApi(Build.VERSION_CODES.O)
class SolicitarCitaViewModel(
    private val citaRepository: CitaRepository,
    private val servicioRepository: ServicioRepository,
    private val empleadoRepository: EmpleadoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitarCitaUiState())
    val uiState: StateFlow<SolicitarCitaUiState> = _uiState

    private val _serviciosDisponibles = MutableStateFlow<List<Servicio>>(emptyList())
    val serviciosDisponibles: StateFlow<List<Servicio>> = _serviciosDisponibles

    private val _empleadosDisponibles = MutableStateFlow<List<Empleado>>(emptyList())
    val empleadosDisponibles: StateFlow<List<Empleado>> = _empleadosDisponibles

    fun inicializarParaEdicion(cita: Cita) {
        viewModelScope.launch {
            try {
                cargarServicios()
                cargarEmpleados()

                kotlinx.coroutines.delay(100)

                val serviciosSeleccionados = _serviciosDisponibles.value.filter { servicio ->
                    cita.citaServicios.any { it.idServicio == servicio.idServicio }
                }

                val empleadoSeleccionado = _empleadosDisponibles.value.find {
                    it.idEmpleado == cita.empleadoId
                }

                cargarHorasDisponibles(cita.fecha)

                _uiState.value = _uiState.value.copy(
                    modoEdicion = true,
                    citaOriginal = cita,
                    serviciosSeleccionados = serviciosSeleccionados,
                    empleadoSeleccionado = empleadoSeleccionado,
                    fechaSeleccionada = cita.fecha,
                    horaSeleccionada = cita.horaInicio,
                    notas = cita.notas ?: "",
                    precioTotal = calcularPrecioTotal(serviciosSeleccionados),
                    duracionTotal = calcularDuracionTotal(serviciosSeleccionados)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar datos de la cita: ${e.message}"
                )
            }
        }
    }

    fun cargarServicios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                _serviciosDisponibles.value = servicioRepository.obtenerTodosLosServicios()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar servicios: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    fun cargarEmpleados() {
        viewModelScope.launch {
            try {
                _empleadosDisponibles.value = empleadoRepository.obtenerTodosLosEmpleados()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar empleados: ${e.message}"
                )
            }
        }
    }


    fun toggleServicio(servicio: Servicio) {
        val serviciosActuales = _uiState.value.serviciosSeleccionados.toMutableList()

        if (serviciosActuales.contains(servicio)) {
            serviciosActuales.remove(servicio)
        } else {
            serviciosActuales.add(servicio)
        }

        _uiState.value = _uiState.value.copy(
            serviciosSeleccionados = serviciosActuales,
            precioTotal = calcularPrecioTotal(serviciosActuales),
            duracionTotal = calcularDuracionTotal(serviciosActuales),
            horaSeleccionada = null // Reset hora cuando cambia duración
        )

        if (_uiState.value.fechaSeleccionada != null && _uiState.value.empleadoSeleccionado != null) {
            cargarHorasDisponibles(_uiState.value.fechaSeleccionada!!)
        }
    }

    fun seleccionarEmpleado(empleado: Empleado) {
        _uiState.value = _uiState.value.copy(
            empleadoSeleccionado = empleado,
            horaSeleccionada = null
        )

        _uiState.value.fechaSeleccionada?.let { fecha ->
            cargarHorasDisponibles(fecha)
        }
    }

    fun seleccionarFecha(fecha: LocalDate) {
        _uiState.value = _uiState.value.copy(
            fechaSeleccionada = fecha,
            horaSeleccionada = null
        )
        cargarHorasDisponibles(fecha)
    }

    fun seleccionarHora(hora: LocalTime) {
        _uiState.value = _uiState.value.copy(horaSeleccionada = hora)
    }

    fun actualizarNotas(notas: String) {
        _uiState.value = _uiState.value.copy(notas = notas)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarHorasDisponibles(fecha: LocalDate) {
        val empleadoSeleccionado = _uiState.value.empleadoSeleccionado
        if (empleadoSeleccionado == null) {
            _uiState.value = _uiState.value.copy(horasDisponibles = emptyList())
            return
        }

        viewModelScope.launch {
            try {
                val zonaMadrid = ZoneId.of("Europe/Madrid")
                val hoyMadrid = LocalDate.now(zonaMadrid)
                var horas = citaRepository.obtenerHorasDisponibles(
                    idEmpleado = empleadoSeleccionado.idEmpleado,
                    fecha = fecha,
                    duracionRequerida = _uiState.value.duracionTotal.takeIf { it > 0 } ?: 30
                )

                if (fecha == hoyMadrid) {
                    val ahoraMadrid = LocalTime.now(zonaMadrid)
                    Log.d("SolicitarCitaVM", "→ Hoy (Madrid), filtrar horas <= $ahoraMadrid. Antes: $horas")
                    horas = horas.filter { it.isAfter(ahoraMadrid) || it == ahoraMadrid }
                    Log.d("SolicitarCitaVM", "→ Después de filtrar: $horas")
                }

                _uiState.value = _uiState.value.copy(
                    horasDisponibles = horas,
                    horaSeleccionada = _uiState.value.horaSeleccionada
                        ?.takeIf { horas.contains(it) }
                )
            } catch (e: Exception) {
                Log.e("SolicitarCitaVM", "Error al cargar horarios disponibles", e)
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar horarios disponibles: ${e.message}",
                    horasDisponibles = emptyList()
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun crearCita() {
        val estado = _uiState.value

        if (!validarCita(estado)) {
            Log.d("SolicitarCitaVM", "❌ Validación falló")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingCita = true)

            try {
                if (estado.modoEdicion && estado.citaOriginal != null) {
                    actualizarCitaExistente(estado)
                } else {
                    crearNuevaCita(estado)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al ${if (estado.modoEdicion) "actualizar" else "crear"} cita: ${e.message}",
                    isCreatingCita = false
                )
            }
        }
    }
    private suspend fun actualizarCitaExistente(estado: SolicitarCitaUiState) {
        val clienteId = sessionManager.getUser()?.idCliente ?: 1
        val horaFin = calcularHoraFin(estado.horaSeleccionada!!, estado.duracionTotal)

        val citaActualizada = estado.citaOriginal!!.copy(
            clienteId = clienteId,
            empleadoId = estado.empleadoSeleccionado!!.idEmpleado,
            fecha = estado.fechaSeleccionada!!,
            horaInicio = estado.horaSeleccionada!!,
            horaFin = horaFin,
            notas = estado.notas.ifBlank { null },
            citaServicios = estado.serviciosSeleccionados.map { servicio ->
                CitaServicio(
                    idCita = estado.citaOriginal.idCita,
                    idServicio = servicio.idServicio,
                    servicio = servicio,
                    precioAplicado = servicio.precio
                )
            }
        )

        Log.d("SolicitarCitaVM", "🔄 Actualizando cita existente...")
        citaRepository.actualizarCita(citaActualizada)

        _uiState.value = _uiState.value.copy(
            citaCreada = true,
            isCreatingCita = false
        )
    }
    private suspend fun crearNuevaCita(estado: SolicitarCitaUiState) {
        val clienteId = sessionManager.getUser()?.idCliente ?: 1
        val horaFin = calcularHoraFin(estado.horaSeleccionada!!, estado.duracionTotal)

        val nuevaCita = Cita(
            idCita = 0,
            clienteId = clienteId,
            empleadoId = estado.empleadoSeleccionado!!.idEmpleado,
            fecha = estado.fechaSeleccionada!!,
            horaInicio = estado.horaSeleccionada!!,
            horaFin = horaFin,
            estado = EstadoCita.PENDIENTE,
            notas = estado.notas.ifBlank { null },
            fechaCreacion = LocalDateTime.now(),
            citaServicios = estado.serviciosSeleccionados.map { servicio ->
                CitaServicio(
                    idCita = 0,
                    idServicio = servicio.idServicio,
                    servicio = servicio,
                    precioAplicado = servicio.precio
                )
            }
        )

        Log.d("SolicitarCitaVM", "🚀 Creando nueva cita...")
        citaRepository.crearCita(nuevaCita)

        _uiState.value = _uiState.value.copy(
            citaCreada = true,
            isCreatingCita = false
        )
    }


    private fun validarCita(estado: SolicitarCitaUiState): Boolean {
        when {
            estado.serviciosSeleccionados.isEmpty() -> {
                _uiState.value = _uiState.value.copy(error = "Debes seleccionar al menos un servicio")
                return false
            }
            estado.empleadoSeleccionado == null -> {
                _uiState.value = _uiState.value.copy(error = "Debes seleccionar un empleado")
                return false
            }
            estado.fechaSeleccionada == null -> {
                _uiState.value = _uiState.value.copy(error = "Debes seleccionar una fecha")
                return false
            }
            estado.horaSeleccionada == null -> {
                _uiState.value = _uiState.value.copy(error = "Debes seleccionar una hora")
                return false
            }
            !estado.horasDisponibles.contains(estado.horaSeleccionada) -> {
                _uiState.value = _uiState.value.copy(error = "La hora seleccionada ya no está disponible")
                return false
            }
        }
        return true
    }

    private fun calcularPrecioTotal(servicios: List<Servicio>): BigDecimal {
        return servicios.sumOf { it.precio }
    }

    private fun calcularDuracionTotal(servicios: List<Servicio>): Int {
        return servicios.sumOf { it.duracion }
    }

    private fun calcularHoraFin(horaInicio: LocalTime, duracionMinutos: Int): LocalTime {
        return horaInicio.plusMinutes(duracionMinutos.toLong())
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearCitaCreada() {
        _uiState.value = _uiState.value.copy(citaCreada = false)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
data class SolicitarCitaUiState(
    val isLoading: Boolean = false,
    val modoEdicion: Boolean = false,
    val citaOriginal: Cita? = null,
    val serviciosSeleccionados: List<Servicio> = emptyList(),
    val empleadoSeleccionado: Empleado? = null,
    val fechaSeleccionada: LocalDate? = null,
    val horaSeleccionada: LocalTime? = null,
    val horasDisponibles: List<LocalTime> = emptyList(),
    val notas: String = "",
    val precioTotal: BigDecimal = BigDecimal.ZERO,
    val duracionTotal: Int = 0,
    val isCreatingCita: Boolean = false,
    val citaCreada: Boolean = false,
    val error: String? = null
)



