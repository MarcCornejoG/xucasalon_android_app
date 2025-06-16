package com.example.xucasalonapp.ui.pedidos.data

import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.ui.pedidos.data.model.CrearPedidoDTO
import com.example.xucasalonapp.ui.pedidos.data.model.CreatePaymentIntentRequest
import com.example.xucasalonapp.ui.pedidos.data.model.PaymentConfirmationResponse
import com.example.xucasalonapp.ui.pedidos.data.model.PaymentIntentResponse
import com.example.xucasalonapp.ui.pedidos.data.model.Pedido
import com.example.xucasalonapp.ui.pedidos.data.model.PedidoCompleteDTO
import com.example.xucasalonapp.ui.pedidos.data.model.toDomain


class PedidoRepositoryImpl(
    private val service: PedidoService
) : PedidoRepository {
    override suspend fun getPedidosPorCliente(idCliente: Int): Resource<List<Pedido>> {
        return try {
            val resp = service.obtenerPedidosPorCliente(idCliente)
            if (resp.isSuccessful) {
                val body = resp.body().orEmpty()
                Resource.Success(body.map { it.toDomain() })
            } else {
                Resource.Error("Error ${resp.code()}: ${resp.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error de red")
        }
    }

    override suspend fun crearPedido(crearPedidoDTO: CrearPedidoDTO): Resource<PedidoCompleteDTO> {
        return try {
            val pedido = service.crearPedido(crearPedidoDTO)
            Resource.Success(pedido)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al crear pedido")
        }
    }

    override suspend fun createPaymentIntent(request: CreatePaymentIntentRequest): Resource<PaymentIntentResponse> {
        return try {
            val response = service.createPaymentIntent(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al crear payment intent")
        }    }

    override suspend fun confirmPayment(paymentIntentId: String): Resource<PaymentConfirmationResponse> {
        return try {
            val response = service.confirmPayment(paymentIntentId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al confirmar pago")
        }    }
}