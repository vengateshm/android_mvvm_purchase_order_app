package com.android.purchaseorder.data.local.room.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.purchaseorder.domain.model.Order
import java.util.*

@Entity(tableName = "order")
data class OrderDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_id")
    val id: Long? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Date?,
    @ColumnInfo(name = "total_amount")
    val totalAmount: Double = 0.0,
)

fun OrderDto.toOrder() = Order(
    id = this.id,
    createdAt = this.createdAt,
    totalAmount = this.totalAmount
)