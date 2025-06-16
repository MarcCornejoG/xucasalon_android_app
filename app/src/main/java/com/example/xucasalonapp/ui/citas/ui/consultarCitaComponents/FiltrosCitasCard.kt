package com.example.xucasalonapp.ui.citas.ui.consultarCitaComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xucasalonapp.ui.citas.data.model.FiltroCitas

@Composable
fun FiltrosCitasCard(
    filtroSeleccionado: FiltroCitas,
    onFiltroChanged: (FiltroCitas) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Filtrar por:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FiltroCitas.values()) { filtro ->
                    FilterChip(
                        selected = filtroSeleccionado == filtro,
                        onClick = { onFiltroChanged(filtro) },
                        label = {
                            Text(
                                text = when (filtro) {
                                    FiltroCitas.TODAS -> "Todas"
                                    FiltroCitas.PROXIMAS -> "Próximas"
                                    FiltroCitas.COMPLETADAS -> "Completadas"
                                    FiltroCitas.CANCELADAS -> "Canceladas"
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (filtro) {
                                    FiltroCitas.TODAS -> Icons.Default.List
                                    FiltroCitas.PROXIMAS -> Icons.Default.Schedule
                                    FiltroCitas.COMPLETADAS -> Icons.Default.Done
                                    FiltroCitas.CANCELADAS -> Icons.Default.Cancel
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}
