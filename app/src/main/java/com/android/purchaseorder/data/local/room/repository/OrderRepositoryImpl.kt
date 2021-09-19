package com.android.purchaseorder.data.local.room.repository

import com.android.purchaseorder.data.local.room.dao.OrderDao
import com.android.purchaseorder.data.local.room.dto.OrderDto
import com.android.purchaseorder.data.local.room.dto.OrderProductCrossRefDto
import com.android.purchaseorder.data.local.room.dto.OrderWithProductsDto
import com.android.purchaseorder.domain.model.OrderedProduct
import com.android.purchaseorder.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

class OrderRepositoryImpl constructor(private val orderDao: OrderDao) : OrderRepository {
    override suspend fun getAllOrderWithProducts(): Flow<List<OrderWithProductsDto>> =
        orderDao.getAllOrdersWithProducts()

    override suspend fun addOrder(
        createdAt: Date,
        orderedProductList: List<OrderedProduct>,
        totalOrderAmt: Double,
    ) {
        val orderId = orderDao.insert(OrderDto(createdAt = createdAt,
            totalAmount = totalOrderAmt))
        orderedProductList.forEach { orderedProduct ->
            orderDao.insertOrderProductCrossRef(OrderProductCrossRefDto(orderId = orderId,
                productId = orderedProduct.product.id!!,
                quantity = orderedProduct.quantity))
        }
    }

    override suspend fun deleteOrder(orderDto: OrderDto) {
        orderDao.delete(orderDto)
        orderDao.deleteOrderProductCrossRefByOrderId(orderDto.id!!)
    }

    override suspend fun clearAllOrderWithProducts() {
        orderDao.deleteAllOrdersWithProducts()
    }
}