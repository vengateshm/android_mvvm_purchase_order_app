package com.android.purchaseorder.presentation.viewModels

import android.content.Context
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.purchaseorder.common.MAX_QUANTITY_DEFAULT
import com.android.purchaseorder.common.MAX_QUANTITY_LIMIT
import com.android.purchaseorder.common.dataStore
import com.android.purchaseorder.data.local.room.dto.toProduct
import com.android.purchaseorder.domain.model.OrderedProduct
import com.android.purchaseorder.domain.repository.OrderRepository
import com.android.purchaseorder.domain.repository.ProductRepository
import com.android.purchaseorder.presentation.uiState.CreateOrderData
import com.android.purchaseorder.presentation.uiState.CreateOrderState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) :
    ViewModel() {

    private var _createOrderState = MutableStateFlow(CreateOrderState())
    val createOrderState: StateFlow<CreateOrderState> = _createOrderState

    fun getAllProducts() {
        _createOrderState.value = CreateOrderState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            productRepository.getAllProducts()
                .catch { throwable ->
                    _createOrderState.value = CreateOrderState(error = throwable.message ?: "")
                }
                .collect { productDtoList ->
                    val productList = productDtoList.map { productDto -> productDto.toProduct() }
                    val maxQuantity = context.dataStore.data
                        .catch {
                            emptyPreferences()
                        }
                        .map { preferences ->
                            // No type safety.
                            preferences[MAX_QUANTITY_LIMIT] ?: MAX_QUANTITY_DEFAULT
                        }.first()
                    _createOrderState.value =
                        CreateOrderState(createOrderData = CreateOrderData(productList, maxQuantity))
                }
        }
    }

    fun createOrder(
        createdAt: Date,
        orderedProductList: List<OrderedProduct>,
        totalOrderAmt: Double,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.addOrder(createdAt, orderedProductList, totalOrderAmt)
        }
    }
}