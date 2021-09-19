package com.android.purchaseorder.data.local.room.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.purchaseorder.domain.model.Product

@Entity(tableName = "product")
data class ProductDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    val id: Long? = null,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "brand")
    val brand: String = "",
)

fun ProductDto.toProduct() = Product(
    id = this.id,
    name = this.name,
    price = this.price,
    brand = this.brand
)