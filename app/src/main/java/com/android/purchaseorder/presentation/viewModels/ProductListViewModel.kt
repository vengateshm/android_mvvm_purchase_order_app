package com.android.purchaseorder.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.purchaseorder.data.local.room.dto.ProductDto
import com.android.purchaseorder.data.local.room.dto.toProduct
import com.android.purchaseorder.domain.model.Product
import com.android.purchaseorder.domain.model.toProductDto
import com.android.purchaseorder.domain.repository.ProductRepository
import com.android.purchaseorder.presentation.uiState.ProductListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    private val _productListState = MutableStateFlow(ProductListState(isLoading = true))
    val productListState: StateFlow<ProductListState> = _productListState

    fun getAllProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _productListState.value = ProductListState(isLoading = true)

            productRepository.getAllProducts()
                .catch { throwable ->
                    _productListState.value = ProductListState(error = throwable.message.toString())
                }
                .collect { productListDto ->
                    _productListState.value =
                        ProductListState(productList = productListDto.map { productDto ->
                            productDto.toProduct()
                        })
                }
        }
    }

    fun addProduct(name: String, price: String) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.addProduct(ProductDto(name = name, price = price.toDouble()))
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.deleteProduct(product.toProductDto())
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.updateProduct(product.toProductDto())
        }
    }
}