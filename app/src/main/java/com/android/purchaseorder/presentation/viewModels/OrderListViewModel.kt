package com.android.purchaseorder.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.purchaseorder.data.local.room.dto.toOrderWithProducts
import com.android.purchaseorder.domain.model.Order
import com.android.purchaseorder.domain.model.toOrderDto
import com.android.purchaseorder.domain.repository.OrderRepository
import com.android.purchaseorder.presentation.uiState.OrderWithProductListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(private val orderRepository: OrderRepository) :
    ViewModel() {
    private val _orderWithProductListState = MutableStateFlow(OrderWithProductListState())
    val orderWithProductListState: StateFlow<OrderWithProductListState> = _orderWithProductListState

    init {
        getAllProducts()
    }

    private fun getAllProducts() {
        _orderWithProductListState.value = OrderWithProductListState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getAllOrderWithProducts()
                .catch { throwable ->
                    _orderWithProductListState.value =
                        OrderWithProductListState(error = throwable.message.toString())
                }
                .collect { orderWithProductsDto ->
                    _orderWithProductListState.value =
                        OrderWithProductListState(orderWithProductList = orderWithProductsDto.map { it.toOrderWithProducts() })
                }
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.deleteOrder(order.toOrderDto())
        }
    }

    fun clearAllOrderWithProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.clearAllOrderWithProducts()
        }
    }
}