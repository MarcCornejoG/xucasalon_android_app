package com.example.xucasalonapp.ui.productos.data

import com.example.xucasalonapp.ui.productos.data.model.Producto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApiService {
    @GET("api/productos")
    suspend fun getAllProducts(): List<Producto>

    @GET("api/productos/buscar/{nombre}")
    suspend fun searchProducts(
        @Path("nombre") productoNombre: String
    ): List<Producto>
}