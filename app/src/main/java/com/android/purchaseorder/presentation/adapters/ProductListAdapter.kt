package com.android.purchaseorder.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.purchaseorder.databinding.ProductListItemBinding
import com.android.purchaseorder.domain.model.Product

class ProductListAdapter(
    private val onItemClick: ((Product) -> Unit)? = null,
    private val onDelete: ((Product) -> Unit)? = null,
) :
    RecyclerView.Adapter<ProductListAdapter.ProductListItemVH>() {

    private val productList = mutableListOf<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListItemVH {
        val binding =
            ProductListItemBinding.inflate(LayoutInflater.from(parent.context))
        return ProductListItemVH(binding, onItemClick, onDelete)
    }

    override fun onBindViewHolder(holder: ProductListItemVH, position: Int) {
        holder.bindProduct(productList[position])
    }

    override fun getItemCount() = productList.size

    fun setProducts(newProductList: List<Product>) {
        val productDiff = ProductListDiffUtil(productList, newProductList)
        val diffResult = DiffUtil.calculateDiff(productDiff)
        productList.clear()
        productList.addAll(newProductList)
        diffResult.dispatchUpdatesTo(this)
    }

    class ProductListItemVH(
        private var binding: ProductListItemBinding,
        private val onItemClick: ((Product) -> Unit)?,
        private val onDelete: ((Product) -> Unit)?,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindProduct(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "\u20B9${product.price}"
            binding.root.setOnClickListener {
                onItemClick?.also { it.invoke(product) }
            }
            binding.ibDelete.setOnClickListener {
                onDelete?.also { it.invoke(product) }
            }
        }
    }

    class ProductListDiffUtil(
        private val oldProductList: List<Product>,
        private val newProductList: List<Product>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldProductList.size

        override fun getNewListSize() = newProductList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldProductList[oldItemPosition].id == newProductList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldProductList[oldItemPosition] == newProductList[newItemPosition]
        }
    }
}