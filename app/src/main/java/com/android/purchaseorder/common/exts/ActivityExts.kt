package com.android.purchaseorder.common.exts

import android.app.Activity
import android.widget.Toast

fun Activity.showToast(strRes: Int) {
    Toast.makeText(this, strRes, Toast.LENGTH_SHORT).show()
}