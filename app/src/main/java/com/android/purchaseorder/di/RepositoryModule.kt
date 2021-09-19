package com.android.purchaseorder.di

import com.android.purchaseorder.data.local.room.dao.OrderDao
import com.android.purchaseorder.data.local.room.dao.ProductDao
import com.android.purchaseorder.data.local.room.repository.OrderRepositoryImpl
import com.android.purchaseorder.data.local.room.repository.ProductRepositoryImpl
import com.android.purchaseorder.domain.repository.OrderRepository
import com.android.purchaseorder.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideProductRepository(productDao: ProductDao): ProductRepository =
        ProductRepositoryImpl(productDao)

    @Provides
    @Singleton
    fun provideOrderRepository(orderDao: OrderDao): OrderRepository =
        OrderRepositoryImpl(orderDao)
}