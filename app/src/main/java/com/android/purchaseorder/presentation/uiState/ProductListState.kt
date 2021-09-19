package com.android.purchaseorder.presentation.uiState

import com.android.purchaseorder.domain.model.Product

data class ProductListState(
    val isLoading: Boolean = false,
    val error: String = "",
    val productList: List<Product> = emptyList(),
)
