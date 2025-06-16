package com.example.xucasalonapp.ui.citas.ui.consultarCitaComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.xucasalonapp.ui.citas.data.model.FiltroCitas

@Composable
fun EmptyStateCard(
    filtroActual: FiltroCitas,
    onSolicitarCita: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = when (filtroActual) {
                    FiltroCitas.TODAS -> Icons.Default.CalendarToday
                    FiltroCitas.PROXIMAS -> Icons.Default.Schedule
                    FiltroCitas.COMPLETADAS -> Icons.Default.Done
                    FiltroCitas.CANCELADAS -> Icons.Default.Cancel
                },
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            Text(
                text = when (filtroActual) {
                    FiltroCitas.TODAS -> "No tienes citas agendadas"
                    FiltroCitas.PROXIMAS -> "No tienes citas próximas"
                    FiltroCitas.COMPLETADAS -> "No tienes citas completadas"
                    FiltroCitas.CANCELADAS -> "No tienes citas canceladas"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Text(
                text = if (filtroActual == FiltroCitas.TODAS) {
                    "¡Agenda tu primera cita para lucir increíble!"
                } else {
                    "Intenta cambiar el filtro para ver otras citas"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            if (filtroActual == FiltroCitas.TODAS) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSolicitarCita,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Solicitar Cita")
                }
            }
        }
    }
}
