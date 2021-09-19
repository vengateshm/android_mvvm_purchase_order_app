package com.android.purchaseorder.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.android.purchaseorder.data.local.room.dto.OrderDto
import com.android.purchaseorder.data.local.room.dto.OrderProductCrossRefDto
import com.android.purchaseorder.data.local.room.dto.OrderWithProductsDto
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderDao : BaseDao<OrderDto>() {
    @Transaction
    @Query("SELECT * FROM `order`")
    abstract fun getAllOrdersWithProducts(): Flow<List<OrderWithProductsDto>>

    @Transaction
    @Insert
    abstract suspend fun insertOrderProductCrossRef(orderProductCrossRefDto: OrderProductCrossRefDto)

    @Transaction
    @Query("DELETE FROM `order`")
    abstract fun deleteAllOrdersWithProducts()

    @Transaction
    @Query("DELETE FROM order_product_cross_ref WHERE order_id IN (:orderId)")
    abstract fun deleteOrderProductCrossRefByOrderId(orderId: Long)

    /*@Transaction
    @Query("SELECT * FROM `order` O INNER JOIN order_product_cross_ref OP ON O.order_id INNER JOIN product P on OP.product_id = P.product_id")
    abstract fun getCombinedData()*/
}