package com.android.purchaseorder.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.purchaseorder.R
import com.android.purchaseorder.databinding.ProductListItemBinding
import com.android.purchaseorder.domain.model.Product

class ProductListArrayAdapter(
    context: Context,
    productList: List<Product>,
) : ArrayAdapter<Product>(context, 0, productList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_item_drop_down, parent, false)
        ProductListItemBinding.inflate(LayoutInflater.from(parent.context))

        val tvProductName = view.findViewById<TextView>(R.id.tvProductName)
        val tvProductPrice = view.findViewById<TextView>(R.id.tvProductPrice)

        tvProductName.text = getItem(position)?.name ?: ""
        tvProductPrice.text = "₹${getItem(position)?.price.toString()}"

        return view
    }
}