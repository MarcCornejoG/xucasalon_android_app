package com.example.xucasalonapp.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Inicio")
    object Citas : BottomNavItem("citas", Icons.Default.Event, "Citas")
    object Productos : BottomNavItem("productos", Icons.Default.ShoppingCart, "Productos")
    object Usuario : BottomNavItem("usuario", Icons.Default.Person, "Usuario")
    object ProductoDetail : BottomNavItem("producto/{id}", Icons.Default.Info, "Detalle")
}