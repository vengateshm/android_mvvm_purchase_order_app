package com.android.purchaseorder.data.local.room.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.android.purchaseorder.domain.model.OrderProductCrossRef

@Entity(tableName = "order_product_cross_ref", primaryKeys = ["order_id", "product_id"])
data class OrderProductCrossRefDto(
    @ColumnInfo(name = "order_id")
    val orderId: Long,
    @ColumnInfo(name = "product_id")
    val productId: Long,
    @ColumnInfo(name = "quantity")
    val quantity: Int,
)

fun OrderProductCrossRefDto.toOrderProductCrossRef() = OrderProductCrossRef(
    orderId = this.orderId,
    productId = this.productId,
    quantity = this.quantity
)