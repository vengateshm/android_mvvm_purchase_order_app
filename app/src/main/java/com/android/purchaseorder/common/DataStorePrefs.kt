package com.android.purchaseorder.common

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(APP_PREFS)

val MAX_QUANTITY_LIMIT = intPreferencesKey("max_quantity_limit")