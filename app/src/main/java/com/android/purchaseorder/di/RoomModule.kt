package com.android.purchaseorder.di

import android.content.Context
import androidx.room.Room
import com.android.purchaseorder.common.ROOM_DB_NAME
import com.android.purchaseorder.data.local.room.AppDatabase
import com.android.purchaseorder.data.local.room.dao.OrderDao
import com.android.purchaseorder.data.local.room.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(applicationContext,
            AppDatabase::class.java, ROOM_DB_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao = appDatabase.orderDao()

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase): ProductDao = appDatabase.productDao()
}