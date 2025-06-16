package com.example.xucasalonapp.ui.productos.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.xucasalonapp.ui.productos.data.model.SortType
import com.example.xucasalonapp.ui.productos.data.model.TipoProducto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarImproved(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Buscar productos...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersPanel(
    selectedTipo: TipoProducto?,
    sortType: SortType,
    onTipoChanged: (TipoProducto?) -> Unit,
    onSortChanged: (SortType) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(onClick = onClearFilters) {
                    Text("Limpiar todo")
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Tipo de producto",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        onClick = { onTipoChanged(null) },
                        label = { Text("Todos") },
                        selected = selectedTipo == null
                    )
                }
                items(TipoProducto.values()) { tipo ->
                    FilterChip(
                        onClick = { onTipoChanged(if (selectedTipo == tipo) null else tipo) },
                        label = { Text(tipo.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        selected = selectedTipo == tipo
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Ordenar por",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    listOf(
                        SortType.NONE to "Por defecto",
                        SortType.PRICE_ASC to "Precio ↑",
                        SortType.PRICE_DESC to "Precio ↓",
                        SortType.NAME_ASC to "Nombre A-Z",
                        SortType.NAME_DESC to "Nombre Z-A",
                        SortType.STOCK_DESC to "Stock"
                    )
                ) { (sort, label) ->
                    FilterChip(
                        onClick = { onSortChanged(sort) },
                        label = { Text(label) },
                        selected = sortType == sort
                    )
                }
            }
        }
    }
}