package com.android.purchaseorder.presentation.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.purchaseorder.R
import com.android.purchaseorder.common.CRUD
import com.android.purchaseorder.common.MAX_PRODUCT_NAME_LENGTH
import com.android.purchaseorder.databinding.ActivityProductListBinding
import com.android.purchaseorder.databinding.DialogAddProductBinding
import com.android.purchaseorder.domain.model.Product
import com.android.purchaseorder.presentation.adapters.ProductListAdapter
import com.android.purchaseorder.presentation.uiState.ProductListState
import com.android.purchaseorder.presentation.viewModels.ProductListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var productCRUDAlertDialog: AlertDialog

    private val productListViewModel: ProductListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupProductListRecyclerView()

        lifecycleScope.launchWhenCreated {
            productListViewModel.productListState.collect {
                setProductListState(it)
            }
        }
        productListViewModel.getAllProducts()

        binding.fabAddProduct.setOnClickListener {
            showProductCRUDActionDialog(CRUD.Add)
        }
    }

    private fun setProductListState(productListState: ProductListState) {
        when {
            productListState.isLoading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE
                binding.rvProductList.visibility = View.GONE
            }
            productListState.error.isNotBlank() -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = productListState.error
                binding.rvProductList.visibility = View.GONE
            }
            else -> {
                binding.progressBar.visibility = View.GONE
                if (productListState.productList.isEmpty()) {
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = getString(R.string.no_products_available)
                    binding.rvProductList.visibility = View.GONE
                } else {
                    binding.tvError.visibility = View.GONE
                    binding.rvProductList.visibility = View.VISIBLE
                    productListAdapter.setProducts(productListState.productList.toMutableList())
                }
            }
        }
    }

    private fun showProductCRUDActionDialog(type: CRUD, product: Product? = null) {
        val binding = DialogAddProductBinding.inflate(layoutInflater)
        val productAlertDialogBuilder = MaterialAlertDialogBuilder(this).setView(binding.root)
        when (type) {
            is CRUD.Add -> {
                productAlertDialogBuilder
                    .setTitle(R.string.add_product)
                    .setPositiveButton(R.string.dialog_add, null)
                    .setNegativeButton(R.string.dialog_cancel) { _, _ -> }
            }
            is CRUD.Update -> {
                product?.also {
                    binding.tetName.setText(it.name)
                    binding.tetPrice.setText(it.price.toInt().toString())
                }
                productAlertDialogBuilder
                    .setTitle(R.string.update_product)
                    .setPositiveButton(R.string.dialog_update, null)
                    .setNegativeButton(R.string.dialog_cancel) { _, _ -> }
            }
            is CRUD.Delete -> {
                binding.root.visibility = View.GONE
                productAlertDialogBuilder
                    .setTitle(R.string.delete_product)
                    .setMessage(getString(R.string.dialog_delete_produt_msg))
                    .setPositiveButton(R.string.dialog_delete) { _, _ ->
                        product?.also {
                            productListViewModel.deleteProduct(product)
                        }
                    }
                    .setNegativeButton(R.string.dialog_close) { _, _ -> }
            }
        }
        productCRUDAlertDialog = productAlertDialogBuilder.show()
        if (type !is CRUD.Delete) {
            productCRUDAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val name = binding.tetName.text.toString()
                    val price = binding.tetPrice.text.toString()
                    if (isProductValid(name, price, binding.tetName, binding.tetPrice)) {
                        if (type is CRUD.Add) {
                            productListViewModel.addProduct(name, price)
                            productCRUDAlertDialog.dismiss()
                        } else if (type is CRUD.Update) {
                            product?.also {
                                val updatedProduct = Product(it.id,
                                    name, price.toDouble(), "")
                                if (product != updatedProduct)
                                    productListViewModel.updateProduct(updatedProduct)
                            }
                            productCRUDAlertDialog.dismiss()
                        }
                    }
                }
        }
    }

    private fun isProductValid(
        name: String,
        price: String,
        tetName: TextInputEditText,
        tetPrice: TextInputEditText,
    ): Boolean {
        return when {
            name.isEmpty() -> {
                tetName.error = getString(R.string.err_field_required)
                false
            }
            name.length > MAX_PRODUCT_NAME_LENGTH -> {
                tetName.error = getString(R.string.product_name_max_chars)
                false
            }
            price.isEmpty() -> {
                tetPrice.error = getString(R.string.err_field_required)
                false
            }
            price.toDouble() <= 0 -> {
                tetPrice.error = getString(R.string.err_price_zero)
                false
            }
            else -> true
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = getString(R.string.product_list)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupProductListRecyclerView() {
        productListAdapter = ProductListAdapter(
            onItemClick = { showProductCRUDActionDialog(CRUD.Update, it) },
            onDelete = { showProductCRUDActionDialog(CRUD.Delete, it) })
        binding.rvProductList.layoutManager = LinearLayoutManager(this)
        binding.rvProductList.adapter = productListAdapter
        binding.rvProductList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.fabAddProduct.visibility = View.GONE
                } else if (dy < 0) {
                    binding.fabAddProduct.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        if (::productCRUDAlertDialog.isInitialized) {
            productCRUDAlertDialog.dismiss()
        }
    }
}