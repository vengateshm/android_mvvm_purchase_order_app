package com.android.purchaseorder.domain.model

data class OrderProductCrossRef(
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
)
