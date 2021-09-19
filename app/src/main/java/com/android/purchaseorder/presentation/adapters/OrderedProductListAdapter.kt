package com.android.purchaseorder.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.purchaseorder.databinding.OrderedProductListItemBinding
import com.android.purchaseorder.domain.model.OrderedProduct

class OrderedProductListAdapter(private val onDelete: ((OrderedProduct) -> Unit)? = null) :
    RecyclerView.Adapter<OrderedProductListAdapter.OrderedProductListItemVH>() {

    var orderedProductList = mutableListOf<OrderedProduct>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedProductListItemVH {
        val binding = OrderedProductListItemBinding.inflate(LayoutInflater.from(parent.context))
        return OrderedProductListItemVH(binding, onDelete)
    }

    override fun onBindViewHolder(holder: OrderedProductListItemVH, position: Int) {
        holder.bindOrderedProduct(orderedProductList[position])
    }

    override fun getItemCount() = orderedProductList.size

    fun addOrderedProduct(orderedProduct: OrderedProduct) {
        val existingOrderedProduct =
            orderedProductList.find { it.product.id == orderedProduct.product.id }
        if (existingOrderedProduct != null) {
            val position = orderedProductList.indexOf(existingOrderedProduct)
            orderedProductList.set(position, orderedProduct)
            notifyItemChanged(position)
        } else {
            orderedProductList.add(orderedProduct)
            notifyItemInserted(orderedProductList.size)
        }
    }

    fun deleteOrderedProduct(orderedProduct: OrderedProduct) {
        if (orderedProductList.isNotEmpty()) {
            val removedPosition = orderedProductList.indexOf(orderedProduct)
            orderedProductList.remove(orderedProduct)
            notifyItemRemoved(removedPosition)
        }
    }

    class OrderedProductListItemVH(
        private var binding: OrderedProductListItemBinding,
        private val onDelete: ((OrderedProduct) -> Unit)?,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindOrderedProduct(orderedProduct: OrderedProduct) {
            binding.tvProductName.text = orderedProduct.product.name
            binding.tvQuantity.text = "Quantity : ${orderedProduct.quantity}"
            binding.tvTotalAmount.text =
                "Total : (${orderedProduct.quantity} X ${orderedProduct.product.price}) \u20B9${orderedProduct.quantity * orderedProduct.product.price}"
            binding.ibDelete.setOnClickListener {
                onDelete?.also {
                    it.invoke(orderedProduct)
                }
            }
        }
    }
}