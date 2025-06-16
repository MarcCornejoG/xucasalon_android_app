package com.example.xucasalonapp.ui.citas.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.citas.data.model.Cita
import com.example.xucasalonapp.ui.citas.ui.consultarCitaComponents.CitaItemCard
import com.example.xucasalonapp.ui.citas.ui.consultarCitaComponents.EmptyStateCard
import com.example.xucasalonapp.ui.citas.ui.consultarCitaComponents.FiltrosCitasCard


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConsultarCitasScreen(
    onNavigateBack: () -> Unit,
    onSolicitarCita: () -> Unit,
    onModificarCita: (Cita) -> Unit,
    sessionManager: SessionManager,
    modifier: Modifier = Modifier
) {
    val viewModel: CitaViewModel = viewModel(
        factory = CitaViewModelFactory(sessionManager)
    )

    val citas by viewModel.citas.collectAsState()
    val filtroSeleccionado by viewModel.filtroSeleccionado.collectAsState()
    val citasFiltradas by viewModel.citasFiltradas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("ConsultarCitas", "Iniciando carga de citas")
        viewModel.cargarCitas()
    }

    LaunchedEffect(citas) {
        Log.d("ConsultarCitas", "Citas actualizadas: ${citas.size}")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ConsultarCitasTopBar(
            onNavigateBack = onNavigateBack
        )

        if (isLoading) {
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
                    FiltrosCitasCard(
                        filtroSeleccionado = filtroSeleccionado,
                        onFiltroChanged = viewModel::cambiarFiltro
                    )
                }

                if (citasFiltradas.isEmpty()) {
                    item {
                        EmptyStateCard(
                            filtroActual = filtroSeleccionado,
                            onSolicitarCita = onSolicitarCita
                        )
                    }
                } else {
                    items(citasFiltradas) { cita ->
                        CitaItemCard(
                            fecha = cita.fecha.formatearFecha(),
                            hora = "${cita.horaInicio.formatearHora()} - ${cita.horaFin.formatearHora()}",
                            servicio = cita.citaServicios.joinToString(", ") { it.servicio.nombre },
                            profesional = viewModel.obtenerNombreEmpleado(cita.empleadoId), // Aquí deberías obtener el nombre real
                            estado = cita.estado,
                            onModificar = {
                                onModificarCita(cita)
                            },
                            onCancelar = { viewModel.cancelarCita(cita.idCita) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultarCitasTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "Mis Citas",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}