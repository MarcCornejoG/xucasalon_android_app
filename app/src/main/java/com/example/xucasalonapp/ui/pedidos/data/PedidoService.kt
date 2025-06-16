package com.example.xucasalonapp.ui.pedidos.data

import com.example.xucasalonapp.ui.pedidos.data.model.CrearPedidoDTO
import com.example.xucasalonapp.ui.pedidos.data.model.CreatePaymentIntentRequest
import com.example.xucasalonapp.ui.pedidos.data.model.PaymentConfirmationResponse
import com.example.xucasalonapp.ui.pedidos.data.model.PaymentIntentResponse
import com.example.xucasalonapp.ui.pedidos.data.model.PedidoCompleteDTO
import com.example.xucasalonapp.ui.pedidos.data.model.PedidoDTO
import com.example.xucasalonapp.ui.pedidos.data.model.PedidoProductoDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PedidoService {
    @GET("api/pedidos/cliente/{idCliente}")
    suspend fun obtenerPedidosPorCliente(
        @Path("idCliente") idCliente: Int
    ): Response<List<PedidoDTO>>

    @POST("api/pedidos")
    suspend fun crearPedido(@Body crearPedidoDTO: CrearPedidoDTO): PedidoCompleteDTO

    @POST("api/pedidos/{idPedido}/productos/{idProducto}")
    suspend fun agregarProductoAPedido(
        @Path("idPedido") idPedido: Int,
        @Path("idProducto") idProducto: Int,
        @Query("cantidad") cantidad: Int
    ): PedidoProductoDTO

    @POST("api/payments/create-payment-intent")
    suspend fun createPaymentIntent(@Body request: CreatePaymentIntentRequest): PaymentIntentResponse

    @POST("api/payments/confirm-payment")
    suspend fun confirmPayment(@Query("paymentIntentId") paymentIntentId: String): PaymentConfirmationResponse

}