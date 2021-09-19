package com.android.purchaseorder.common

const val ROOM_DB_NAME = "purchase-order-db"
const val APP_PREFS = "app_prefs"
const val ORDER_LIST_DATE_FORMAT = "dd MMM, yyyy"
const val MAX_QUANTITY_DEFAULT = 5
const val MAX_QUANTITY = 1000
const val MIN_QUANTITY = 1
const val MAX_PRODUCT_NAME_LENGTH = 25

sealed class CRUD {
    object Add : CRUD()
    object Update : CRUD()
    object Delete : CRUD()
}