package com.android.purchaseorder.data.local.room.converters

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun timeStampToDate(timeInMillis: Long?): Date? {
        return timeInMillis?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}