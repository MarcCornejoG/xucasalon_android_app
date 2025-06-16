package com.example.xucasalonapp.ui.citas.ui.consultarCitaComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xucasalonapp.ui.citas.data.model.EstadoCita
import com.example.xucasalonapp.ui.citas.data.model.Tuple4


@Composable
fun CitaStatusChip(
    estado: EstadoCita,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor, text, icon) = when (estado) {
        EstadoCita.CONFIRMADA -> Tuple4(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary,
            "Confirmada",
            Icons.Default.CheckCircle
        )
        EstadoCita.PENDIENTE -> Tuple4(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary,
            "Pendiente",
            Icons.Default.Schedule
        )
        EstadoCita.CANCELADA -> Tuple4(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError,
            "Cancelada",
            Icons.Default.Cancel
        )
        EstadoCita.FINALIZADA -> Tuple4(
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary,
            "Completada",
            Icons.Default.Done
        )
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = contentColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = contentColor
            )
        }
    }
}