package com.android.purchaseorder.presentation.uiState

import com.android.purchaseorder.domain.model.OrderWithProducts

data class OrderWithProductListState(
    val isLoading: Boolean = false,
    val error: String = "",
    val orderWithProductList: List<OrderWithProducts> = emptyList(),
)
