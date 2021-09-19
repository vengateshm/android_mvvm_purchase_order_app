package com.android.purchaseorder.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.purchaseorder.data.local.room.converters.Converters
import com.android.purchaseorder.data.local.room.dao.OrderDao
import com.android.purchaseorder.data.local.room.dao.ProductDao
import com.android.purchaseorder.data.local.room.dto.OrderDto
import com.android.purchaseorder.data.local.room.dto.OrderProductCrossRefDto
import com.android.purchaseorder.data.local.room.dto.ProductDto

@Database(entities = [OrderProductCrossRefDto::class,
    OrderDto::class, ProductDto::class],
    version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun productDao(): ProductDao
}