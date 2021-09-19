package com.android.purchaseorder.domain.repository

import com.android.purchaseorder.data.local.room.dto.OrderDto
import com.android.purchaseorder.data.local.room.dto.OrderWithProductsDto
import com.android.purchaseorder.domain.model.OrderedProduct
import kotlinx.coroutines.flow.Flow
import java.util.*

interface OrderRepository {
    suspend fun getAllOrderWithProducts(): Flow<List<OrderWithProductsDto>>
    suspend fun addOrder(
        createdAt: Date,
        orderedProductList: List<OrderedProduct>,
        totalOrderAmt: Double,
    )

    suspend fun clearAllOrderWithProducts()
    suspend fun deleteOrder(orderDto: OrderDto)
}