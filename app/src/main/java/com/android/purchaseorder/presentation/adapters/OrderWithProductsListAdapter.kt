package com.android.purchaseorder.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.purchaseorder.common.ORDER_LIST_DATE_FORMAT
import com.android.purchaseorder.common.exts.toFormattedDate
import com.android.purchaseorder.databinding.OrderWithProductsListItemBinding
import com.android.purchaseorder.domain.model.Order
import com.android.purchaseorder.domain.model.OrderWithProducts

class OrderWithProductsListAdapter(private val onDelete: (Order) -> Unit) :
    RecyclerView.Adapter<OrderWithProductsListAdapter.OrderWithProductsListItemVH>() {

    private val orderWithProductsList = mutableListOf<OrderWithProducts>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderWithProductsListItemVH {
        val binding = OrderWithProductsListItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return OrderWithProductsListItemVH(onDelete, binding)
    }

    override fun onBindViewHolder(holder: OrderWithProductsListItemVH, position: Int) {
        holder.bindOrderWithProducts(orderWithProductsList[position])
    }

    override fun getItemCount() = orderWithProductsList.size

    fun setOrderWithProducts(newWithProductsList: List<OrderWithProducts>) {
        val productDiff = OrderWithProductsDiffUtil(orderWithProductsList, newWithProductsList)
        val diffResult = DiffUtil.calculateDiff(productDiff)
        orderWithProductsList.clear()
        orderWithProductsList.addAll(newWithProductsList)
        diffResult.dispatchUpdatesTo(this)
    }

    class OrderWithProductsListItemVH(
        private val onDelete: (Order) -> Unit,
        private var binding: OrderWithProductsListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindOrderWithProducts(orderWithProducts: OrderWithProducts) {
            binding.tvOrderCreatedAt.text =
                orderWithProducts.order.createdAt?.toFormattedDate(ORDER_LIST_DATE_FORMAT)
            binding.tvTotalAmount.text =
                "Total Amount : \u20B9${orderWithProducts.order.totalAmount}"
            buildString {
                orderWithProducts.products.forEachIndexed { index, product ->
                    // Get product quantity
                    val qty =
                        orderWithProducts.orderProductCrossRef.find { it.productId == product.id }?.quantity
                    append("${qty ?: ""} x ${product.name}")
                    if (index < orderWithProducts.products.size - 1) {
                        append("\n")
                    }
                }
            }.also {
                binding.tvProductsList.text = it
            }
            binding.ivDelete.setOnClickListener {
                onDelete.invoke(orderWithProducts.order)
            }
        }
    }

    class OrderWithProductsDiffUtil(
        private val oldList: List<OrderWithProducts>,
        private val newList: List<OrderWithProducts>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].order.id == newList[newItemPosition].order.id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].order == newList[newItemPosition].order
    }
}