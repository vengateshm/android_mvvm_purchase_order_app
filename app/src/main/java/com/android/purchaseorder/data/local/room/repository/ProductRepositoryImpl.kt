package com.android.purchaseorder.data.local.room.repository

import com.android.purchaseorder.data.local.room.dao.ProductDao
import com.android.purchaseorder.data.local.room.dto.ProductDto
import com.android.purchaseorder.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val productDao: ProductDao) :
    ProductRepository {

    override fun addProduct(productDto: ProductDto) {
        productDao.insert(productDto)
    }

    override fun updateProduct(productDto: ProductDto) {
        productDao.update(productDto)
    }

    override fun deleteProduct(productDto: ProductDto) {
        productDao.delete(productDto)
    }

    override fun getAllProducts(): Flow<List<ProductDto>> {
        return productDao.getAllProducts()
    }
}