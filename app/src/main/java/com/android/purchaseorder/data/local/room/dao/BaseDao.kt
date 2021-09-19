package com.android.purchaseorder.data.local.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

abstract class BaseDao<T> {
    @Insert
    abstract fun insert(t: T): Long

    @Update
    abstract fun update(t: T)

    @Delete
    abstract fun delete(t: T)
}