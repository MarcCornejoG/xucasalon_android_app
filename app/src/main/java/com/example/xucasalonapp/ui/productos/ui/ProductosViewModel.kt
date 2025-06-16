package com.example.xucasalonapp.ui.productos.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xucasalonapp.data.SessionManager
import com.example.xucasalonapp.ui.productos.data.ProductApiService
import com.example.xucasalonapp.ui.productos.data.ProductoRepositoryApiImpl
import com.example.xucasalonapp.ui.productos.data.model.Producto
import com.example.xucasalonapp.ui.productos.data.model.SortType
import com.example.xucasalonapp.ui.productos.data.model.TipoProducto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductosViewModel(sessionManager: SessionManager) : ViewModel() {
    private val repo: ProductApiService = ProductoRepositoryApiImpl(sessionManager)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _allProductos = MutableStateFlow<List<Producto>>(emptyList())
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos


    private val _selectedTipo = MutableStateFlow<TipoProducto?>(null)
    val selectedTipo: StateFlow<TipoProducto?> = _selectedTipo

    private val _sortType = MutableStateFlow(SortType.NONE)
    val sortType: StateFlow<SortType> = _sortType

    private val _showFilters = MutableStateFlow(false)
    val showFilters: StateFlow<Boolean> = _showFilters

    init {
        loadProducts()
    }

    fun onQueryChanged(q: String) {
        _query.value = q
        applyFiltersAndSort()
    }

    fun onTipoChanged(tipo: TipoProducto?) {
        _selectedTipo.value = tipo
        applyFiltersAndSort()
    }

    fun onSortChanged(sort: SortType) {
        _sortType.value = sort
        applyFiltersAndSort()
    }

    fun toggleFilters() {
        _showFilters.value = !_showFilters.value
    }

    fun clearFilters() {
        _selectedTipo.value = null
        _sortType.value = SortType.NONE
        _query.value = ""
        applyFiltersAndSort()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val allProducts = repo.getAllProducts()
            _allProductos.value = allProducts
            _productos.value = allProducts
        }
    }

    private fun applyFiltersAndSort() {
        viewModelScope.launch {
            var filteredProducts = _allProductos.value

            if (_query.value.isNotBlank()) {
                filteredProducts = filteredProducts.filter {
                    it.nombre.contains(_query.value, ignoreCase = true) ||
                            it.descripcion?.contains(_query.value, ignoreCase = true) == true
                }
            }

            _selectedTipo.value?.let { tipo ->
                filteredProducts = filteredProducts.filter { it.tipo == tipo }
            }

            filteredProducts = when (_sortType.value) {
                SortType.PRICE_ASC -> filteredProducts.sortedBy { it.precio }
                SortType.PRICE_DESC -> filteredProducts.sortedByDescending { it.precio }
                SortType.NAME_ASC -> filteredProducts.sortedBy { it.nombre }
                SortType.NAME_DESC -> filteredProducts.sortedByDescending { it.nombre }
                SortType.STOCK_DESC -> filteredProducts.sortedByDescending { it.stock }
                SortType.NONE -> filteredProducts
            }

            _productos.value = filteredProducts
        }

    }
}
