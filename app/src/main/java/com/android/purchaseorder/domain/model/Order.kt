package com.android.purchaseorder.domain.model

import com.android.purchaseorder.data.local.room.dto.OrderDto
import java.util.*

data class Order(
    val id: Long? = null,
    val createdAt: Date?,
    val totalAmount: Double = 0.0,
)

fun Order.toOrderDto() = OrderDto(
    id = this.id,
    createdAt = this.createdAt,
    totalAmount = this.totalAmount
)
