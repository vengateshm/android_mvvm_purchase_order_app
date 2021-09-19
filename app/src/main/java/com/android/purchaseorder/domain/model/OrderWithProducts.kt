package com.android.purchaseorder.domain.model

data class OrderWithProducts(
    val order: Order,
    val products: List<Product>,
    val orderProductCrossRef: List<OrderProductCrossRef>,
)
