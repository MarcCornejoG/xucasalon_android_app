package com.example.xucasalonapp.ui.productos.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.data.api.ApiClient
import com.example.xucasalonapp.ui.productos.data.model.Producto

class ProductoRepositoryApiImpl(sessionManager: SessionManager) : ProductApiService {

    @RequiresApi(Build.VERSION_CODES.O)
    private val api: ProductApiService =
        ApiClient.getInstance(sessionManager).createService(ProductApiService::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAllProducts(): List<Producto> = api.getAllProducts()
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun searchProducts(productoNombre: String): List<Producto> = api.searchProducts(productoNombre)






}