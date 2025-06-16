package com.example.xucasalonapp.ui.citas.ui


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.citas.data.CitaRepositoryApiImpl
import com.example.xucasalonapp.ui.citas.data.model.Cita
import com.example.xucasalonapp.ui.citas.data.model.EstadoCita
import com.example.xucasalonapp.ui.citas.data.model.FiltroCitas
import com.example.xucasalonapp.ui.empleados.EmpleadoRepository
import com.example.xucasalonapp.ui.empleados.EmpleadoRepositoryApiImpl
import com.example.xucasalonapp.ui.empleados.data.Empleado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class CitaViewModel(
    private val repo: CitaRepositoryApiImpl,
    private val empleadoRepo: EmpleadoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas

    private val _empleados = MutableStateFlow<Map<Int, Empleado>>(emptyMap())
    val empleados: StateFlow<Map<Int, Empleado>> = _empleados

    private val _filtroSeleccionado = MutableStateFlow(FiltroCitas.TODAS)
    val filtroSeleccionado: StateFlow<FiltroCitas> = _filtroSeleccionado

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val citasFiltradas: StateFlow<List<Cita>> = combine(
        _citas,
        _filtroSeleccionado
    ) { citas, filtro ->
        when (filtro) {
            FiltroCitas.TODAS -> citas
            FiltroCitas.PROXIMAS ->
                citas.filter { it.estado == EstadoCita.CONFIRMADA || it.estado == EstadoCita.PENDIENTE }
            FiltroCitas.COMPLETADAS ->
                citas.filter { it.estado == EstadoCita.FINALIZADA }
            FiltroCitas.CANCELADAS ->
                citas.filter { it.estado == EstadoCita.CANCELADA }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        cargarCitas()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarCitas() {
        val user = sessionManager.getUser() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val citasResponse = repo.getCitasPorCliente(user.idCliente)
                Log.d("CitaViewModel", "Citas recibidas: ${citasResponse.size}")
                _citas.value = citasResponse

                cargarEmpleados(citasResponse)
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error cargando citas", e)
                _citas.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun cargarEmpleados(citas: List<Cita>) {
        try {
            val empleadosIds = citas.map { it.empleadoId }.distinct()

            val empleadosMap = mutableMapOf<Int, Empleado>()

            empleadosIds.forEach { empleadoId ->
                try {
                    val empleado = empleadoRepo.obtenerEmpleadoPorId(empleadoId)
                    empleadosMap[empleadoId] = empleado
                } catch (e: Exception) {
                    Log.e("CitaViewModel", "Error cargando empleado $empleadoId", e)
                }
            }

            _empleados.value = empleadosMap
        } catch (e: Exception) {
            Log.e("CitaViewModel", "Error cargando empleados", e)
        }
    }

    fun obtenerNombreEmpleado(empleadoId: Int): String {
        return _empleados.value[empleadoId]?.nombreCompleto ?: "Empleado $empleadoId"
    }

    fun cambiarFiltro(nuevoFiltro: FiltroCitas) {
        _filtroSeleccionado.value = nuevoFiltro
    }



    fun cancelarCita(idCita: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                repo.cancelarCita(idCita)

                _citas.value = _citas.value.map { cita ->
                    if (cita.idCita == idCita) {
                        cita.copy(estado = EstadoCita.CANCELADA)
                    } else {
                        cita
                    }
                }

                Log.d("CitaViewModel", "Cita $idCita cancelada exitosamente")

            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error cancelando cita $idCita", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun modificarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                val citaActualizada = repo.actualizarCita(cita)

                // Actualizar la lista local
                _citas.value = _citas.value.map { citaActual ->
                    if (citaActual.idCita == citaActualizada.idCita) {
                        citaActualizada
                    } else {
                        citaActual
                    }
                }
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error actualizando cita", e)
            }
        }
    }
    fun obtenerCitaPorId(idCita: Int): Cita? {
        return _citas.value.find { it.idCita == idCita }
    }

}

class CitaViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CitaViewModel(
                repo = CitaRepositoryApiImpl(sessionManager),
                empleadoRepo = EmpleadoRepositoryApiImpl(sessionManager),
                sessionManager = sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.formatearFecha(): String =
    this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

@RequiresApi(Build.VERSION_CODES.O)
fun LocalTime.formatearHora(): String =
    this.format(DateTimeFormatter.ofPattern("HH:mm"))
