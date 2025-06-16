package com.example.xucasalonapp.ui.citas.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.citas.data.model.Cita
import com.example.xucasalonapp.ui.citas.ui.solicitarCitasComponents.FechaSelectionCard
import com.example.xucasalonapp.ui.citas.ui.solicitarCitasComponents.HoraSelectionCard
import com.example.xucasalonapp.ui.citas.ui.solicitarCitasComponents.NotasCard
import com.example.xucasalonapp.ui.citas.ui.solicitarCitasComponents.ResumenCitaCard
import com.example.xucasalonapp.ui.citas.ui.solicitarCitasComponents.ServiciosSelectionCard
import com.example.xucasalonapp.ui.citas.ui.solicitarCitasComponents.SolicitarCitaTopBar
import com.example.xucasalonapp.ui.empleados.ui.EmpleadoSelectionCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SolicitarCitaScreen(
    onNavigateBack: () -> Unit,
    onCitaCreada: () -> Unit,
    sessionManager: SessionManager,
    citaParaEditar: Cita? = null,
    modifier: Modifier = Modifier
) {
    val viewModel: SolicitarCitaViewModel = viewModel(
        factory = SolicitarCitaViewModelFactory(sessionManager)
    )

    val uiState by viewModel.uiState.collectAsState()
    val serviciosDisponibles by viewModel.serviciosDisponibles.collectAsState()
    val empleadosDisponibles by viewModel.empleadosDisponibles.collectAsState()

    LaunchedEffect(citaParaEditar) {
        if (citaParaEditar != null) {
            viewModel.inicializarParaEdicion(citaParaEditar)
        } else {
            viewModel.cargarServicios()
            viewModel.cargarEmpleados()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.cargarServicios()
        viewModel.cargarEmpleados()
    }

    LaunchedEffect(uiState.citaCreada) {
        Log.d("SolicitarCitaScreen", "🔍 LaunchedEffect triggered - citaCreada: ${uiState.citaCreada}")

        if (uiState.citaCreada) {
            Log.d("SolicitarCitaScreen", "🎉 Cita ${if (uiState.modoEdicion) "actualizada" else "creada"}! Navegando...")
            onCitaCreada()
            viewModel.clearCitaCreada()
            Log.d("SolicitarCitaScreen", "✅ Navegación completada")
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SolicitarCitaTopBar(onNavigateBack = onNavigateBack)

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ServiciosSelectionCard(
                        servicios = serviciosDisponibles,
                        serviciosSeleccionados = uiState.serviciosSeleccionados,
                        onServicioToggle = viewModel::toggleServicio
                    )
                }

                if (uiState.serviciosSeleccionados.isNotEmpty()) {
                    item {
                        EmpleadoSelectionCard(
                            empleados = empleadosDisponibles,
                            empleadoSeleccionado = uiState.empleadoSeleccionado,
                            onEmpleadoSelected = viewModel::seleccionarEmpleado
                        )
                    }
                }

                if (uiState.empleadoSeleccionado != null) {
                    item {
                        FechaSelectionCard(
                            fechaSeleccionada = uiState.fechaSeleccionada,
                            onFechaSelected = viewModel::seleccionarFecha
                        )
                    }
                }

                if (uiState.fechaSeleccionada != null) {
                    item {
                        HoraSelectionCard(
                            horasDisponibles = uiState.horasDisponibles,
                            horaSeleccionada = uiState.horaSeleccionada,
                            onHoraSelected = viewModel::seleccionarHora
                        )
                    }
                }

                if (uiState.horaSeleccionada != null) {
                    item {
                        NotasCard(
                            notas = uiState.notas,
                            onNotasChanged = viewModel::actualizarNotas
                        )
                    }
                }

                if (uiState.horaSeleccionada != null) {
                    item {
                        ResumenCitaCard(
                            serviciosSeleccionados = uiState.serviciosSeleccionados,
                            empleadoSeleccionado = uiState.empleadoSeleccionado!!, // ✅ Pasar empleado
                            fecha = uiState.fechaSeleccionada!!,
                            hora = uiState.horaSeleccionada!!,
                            precioTotal = uiState.precioTotal,
                            duracionTotal = uiState.duracionTotal,
                            onConfirmar = viewModel::crearCita,
                            isLoading = uiState.isCreatingCita
                        )
                    }
                }
            }
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            println("Error: $error")
            viewModel.clearError()
        }
    }
}