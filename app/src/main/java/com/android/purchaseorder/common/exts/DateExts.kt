package com.android.purchaseorder.common.exts

import java.text.SimpleDateFormat
import java.util.*

fun Date.toFormattedDate(format: String): String {
    return try {
        SimpleDateFormat(format, Locale.getDefault()).format(this)
    } catch (e: Exception) {
        "N/A"
    }
}