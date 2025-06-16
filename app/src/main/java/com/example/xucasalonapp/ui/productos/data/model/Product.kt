package com.example.xucasalonapp.ui.productos.data.model

import java.math.BigDecimal

data class Producto(
    val idProducto: Int,
    val nombre: String,
    val descripcion: String?,
    val tipo: TipoProducto,
    val precio: BigDecimal,
    val stock: Int,
    val codigoBarras: String?,
    val imagenUrl: String?,
    val estado: EstadoProducto
)