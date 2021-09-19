package com.android.purchaseorder.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.android.purchaseorder.data.local.room.dto.ProductDto
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao : BaseDao<ProductDto>() {
    @Query("SELECT * FROM product")
    abstract fun getAllProducts(): Flow<List<ProductDto>>
}