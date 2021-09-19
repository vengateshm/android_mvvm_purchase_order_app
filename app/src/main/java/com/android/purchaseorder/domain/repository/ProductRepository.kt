package com.android.purchaseorder.domain.repository

import com.android.purchaseorder.data.local.room.dto.ProductDto
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun addProduct(productDto: ProductDto)
    fun updateProduct(productDto: ProductDto)
    fun deleteProduct(productDto: ProductDto)
    fun getAllProducts(): Flow<List<ProductDto>>
}