package com.android.purchaseorder.domain.model

import com.android.purchaseorder.data.local.room.dto.ProductDto

data class Product(val id: Long? = null, val name: String, val price: Double, val brand: String)

fun Product.toProductDto() = ProductDto(
    id = this.id,
    name = this.name,
    price = this.price,
    brand = this.brand
)
