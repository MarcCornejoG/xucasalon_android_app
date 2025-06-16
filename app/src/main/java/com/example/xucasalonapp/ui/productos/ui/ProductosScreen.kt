package com.example.xucasalonapp.ui.productos.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.pedidos.ui.CarritoViewModel
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.xucasalonapp.ui.productos.ui.components.EmptyStateView
import com.example.xucasalonapp.ui.productos.ui.components.FiltersPanel
import com.example.xucasalonapp.ui.productos.ui.components.ProductCardImproved
import com.example.xucasalonapp.ui.productos.ui.components.SearchBarImproved


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    sessionManager: SessionManager,
    carritoViewModel: CarritoViewModel
) {
    val viewModel = remember { ProductosViewModel(sessionManager) }
    val productos by viewModel.productos.collectAsState()
    val query by viewModel.query.collectAsState()
    val selectedTipo by viewModel.selectedTipo.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val showFilters by viewModel.showFilters.collectAsState()
    val itemCount by carritoViewModel.itemCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
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
                        text = "Productos",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${productos.size} productos",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        IconButton(
                            onClick = { viewModel.toggleFilters() }
                        ) {
                            Icon(
                                imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                                contentDescription = "Filtros",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                SearchBarImproved(query, viewModel::onQueryChanged)

                AnimatedVisibility(
                    visible = showFilters,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    FiltersPanel(
                        selectedTipo = selectedTipo,
                        sortType = sortType,
                        onTipoChanged = viewModel::onTipoChanged,
                        onSortChanged = viewModel::onSortChanged,
                        onClearFilters = viewModel::clearFilters
                    )
                }
            }
        }

        if (productos.isEmpty()) {
            EmptyStateView()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(productos, key = { it.idProducto }) { producto ->
                    ProductCardImproved(
                        product = producto,
                        modifier = Modifier.animateItemPlacement()
                    ) {
                        navController.navigate("producto/${producto.idProducto}")
                    }
                }
            }
        }
    }
}


