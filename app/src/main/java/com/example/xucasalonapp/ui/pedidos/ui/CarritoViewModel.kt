package com.example.xucasalonapp.ui.pedidos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xucasalonapp.core.Resource
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.pedidos.data.PedidoRepository
import com.example.xucasalonapp.ui.pedidos.data.model.CarritoItem
import com.example.xucasalonapp.ui.pedidos.data.model.CrearPedidoDTO
import com.example.xucasalonapp.ui.pedidos.data.model.CreatePaymentIntentRequest
import com.example.xucasalonapp.ui.pedidos.data.model.PaymentIntentResponse
import com.example.xucasalonapp.ui.pedidos.data.model.PedidoCompleteDTO
import com.example.xucasalonapp.ui.productos.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

class CarritoViewModel(
    private val sessionManager: SessionManager,
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    val items: StateFlow<List<CarritoItem>> = _items

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.None)
    val paymentState: StateFlow<PaymentState> = _paymentState

    val total: StateFlow<BigDecimal> = _items.map { items ->
        items.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.subtotal) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BigDecimal.ZERO
    )

    val itemCount: StateFlow<Int> = _items.map { items ->
        items.sumOf { it.cantidad }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    suspend fun createPaymentIntent(): Result<PaymentIntentResponse> {
        return try {
            _paymentState.value = PaymentState.CreatingPaymentIntent

            val cliente = sessionManager.getUser()
            if (cliente == null || _items.value.isEmpty()) {
                _paymentState.value = PaymentState.PaymentError("Cliente no logueado o carrito vacío")
                return Result.failure(Exception("Cliente no logueado o carrito vacío"))
            }

            val pedidoResult = crearPedido()
            if (pedidoResult.isFailure) {
                _paymentState.value = PaymentState.PaymentError("Error al crear pedido")
                return Result.failure(pedidoResult.exceptionOrNull()!!)
            }

            val pedido = pedidoResult.getOrNull()!!

            val amountInCents = (total.value.multiply(BigDecimal(100))).toLong()

            val request = CreatePaymentIntentRequest(
                amount = amountInCents,
                currency = "eur",
                idPedido = pedido.idPedido
            )

            val response = pedidoRepository.createPaymentIntent(request)

            when (response) {
                is Resource.Success -> {
                    _paymentState.value = PaymentState.PaymentIntentCreated(
                        response.data.clientSecret,
                        response.data.paymentIntentId
                    )
                    Result.success(response.data)
                }
                is Resource.Error -> {
                    _paymentState.value = PaymentState.PaymentError(response.message)
                    Result.failure(Exception(response.message))
                }
                else -> {
                    _paymentState.value = PaymentState.PaymentError("Error desconocido")
                    Result.failure(Exception("Error desconocido"))
                }
            }
        } catch (e: Exception) {
            _paymentState.value = PaymentState.PaymentError(e.message ?: "Error desconocido")
            Result.failure(e)
        }
    }

    fun addToCart(producto: Producto, cantidad: Int = 1) {
        val currentItems = _items.value.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.producto.idProducto == producto.idProducto }

        if (existingItemIndex >= 0) {
            currentItems[existingItemIndex] = currentItems[existingItemIndex].copy(
                cantidad = currentItems[existingItemIndex].cantidad + cantidad
            )
        } else {
            currentItems.add(CarritoItem(producto, cantidad))
        }

        _items.value = currentItems
    }

    fun updateQuantity(productoId: Int, nuevaCantidad: Int) {
        val currentItems = _items.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.producto.idProducto == productoId }

        if (itemIndex >= 0) {
            if (nuevaCantidad <= 0) {
                currentItems.removeAt(itemIndex)
            } else {
                currentItems[itemIndex] = currentItems[itemIndex].copy(cantidad = nuevaCantidad)
            }
            _items.value = currentItems
        }
    }

    fun removeFromCart(productoId: Int) {
        _items.value = _items.value.filter { it.producto.idProducto != productoId }
    }

    fun clearCart() {
        _items.value = emptyList()
    }

    suspend fun crearPedido(): Result<PedidoCompleteDTO> {
        return try {
            val cliente = sessionManager.getUser()
            if (cliente == null || _items.value.isEmpty()) {
                return Result.failure(Exception("Cliente no logueado o carrito vacío"))
            }

            val productosYCantidades = _items.value.associate {
                it.producto.idProducto to it.cantidad
            }

            val crearPedidoDTO = CrearPedidoDTO().apply {
                idCliente = cliente.idCliente
                this.productosYCantidades = productosYCantidades
            }

            when (val result = pedidoRepository.crearPedido(crearPedidoDTO)) {
                is Resource.Success -> {
                    clearCart()
                    Result.success(result.data)
                }
                is Resource.Error -> {
                    Result.failure(Exception(result.message))
                }
                is Resource.Loading -> {
                    Result.failure(Exception("Operación en curso"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmPayment(paymentIntentId: String): Result<String> {
        return try {
            _paymentState.value = PaymentState.ProcessingPayment

            val result = pedidoRepository.confirmPayment(paymentIntentId)

            when (result) {
                is Resource.Success -> {
                    if (result.data.status == "succeeded") {
                        clearCart()
                        _paymentState.value = PaymentState.None
                        Result.success("Pago confirmado exitosamente")
                    } else {
                        _paymentState.value = PaymentState.PaymentError("Pago no completado")
                        Result.failure(Exception("Pago no completado"))
                    }
                }
                is Resource.Error -> {
                    _paymentState.value = PaymentState.PaymentError(result.message)
                    Result.failure(Exception(result.message))
                }
                else -> {
                    _paymentState.value = PaymentState.PaymentError("Error desconocido")
                    Result.failure(Exception("Error desconocido"))
                }
            }
        } catch (e: Exception) {
            _paymentState.value = PaymentState.PaymentError(e.message ?: "Error desconocido")
            Result.failure(e)
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.None
    }
    sealed class PaymentState {
        object None : PaymentState()
        object CreatingPaymentIntent : PaymentState()
        data class PaymentIntentCreated(val clientSecret: String, val paymentIntentId: String) : PaymentState()
        object ProcessingPayment : PaymentState()
        data class PaymentSuccess(val pedidoCompleto: PedidoCompleteDTO) : PaymentState()
        data class PaymentError(val message: String) : PaymentState()
    }
}




