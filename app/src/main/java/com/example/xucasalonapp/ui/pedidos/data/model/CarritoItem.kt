package com.example.xucasalonapp.ui.pedidos.data.model

import com.example.xucasalonapp.ui.productos.data.model.Producto
import java.math.BigDecimal

data class CarritoItem(
    val producto: Producto,
    val cantidad: Int = 1
){
    val subtotal: BigDecimal
        get() = producto.precio.multiply(BigDecimal(cantidad))
}
