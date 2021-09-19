package com.android.purchaseorder.data.local.room.dto

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.android.purchaseorder.domain.model.OrderWithProducts

data class OrderWithProductsDto(
    @Embedded
    val orderDto: OrderDto,
    @Relation(
        parentColumn = "order_id",
        entityColumn = "product_id",
        associateBy = Junction(OrderProductCrossRefDto::class)
    )
    val products: List<ProductDto>,
    @Relation(
        parentColumn = "order_id",
        entityColumn = "order_id",
        associateBy = Junction(OrderProductCrossRefDto::class)
    )
    val orderProductCrossRefs: List<OrderProductCrossRefDto>,
)

fun OrderWithProductsDto.toOrderWithProducts() = OrderWithProducts(
    order = orderDto.toOrder(),
    products = this.products.map { it.toProduct() },
    orderProductCrossRef = this.orderProductCrossRefs.distinct().map { it.toOrderProductCrossRef() }
)