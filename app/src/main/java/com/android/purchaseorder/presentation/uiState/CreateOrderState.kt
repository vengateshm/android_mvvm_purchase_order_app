package com.android.purchaseorder.presentation.uiState

import com.android.purchaseorder.domain.model.Product

data class CreateOrderState(
    val isLoading: Boolean = false,
    val error: String = "",
    val createOrderData: CreateOrderData? = null,
)

data class CreateOrderData(
    val productList: List<Product>,
    val maxQuantityLimit: Int,
)

