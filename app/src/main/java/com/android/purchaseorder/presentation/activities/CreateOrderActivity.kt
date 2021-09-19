package com.android.purchaseorder.presentation.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TableRow.LayoutParams
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.purchaseorder.R
import com.android.purchaseorder.common.exts.gone
import com.android.purchaseorder.common.exts.show
import com.android.purchaseorder.common.exts.showToast
import com.android.purchaseorder.common.exts.toFormattedDate
import com.android.purchaseorder.databinding.ActivityCreateOrderBinding
import com.android.purchaseorder.databinding.DialogOrderSummaryBinding
import com.android.purchaseorder.domain.model.OrderedProduct
import com.android.purchaseorder.domain.model.Product
import com.android.purchaseorder.presentation.adapters.OrderedProductListAdapter
import com.android.purchaseorder.presentation.adapters.ProductListArrayAdapter
import com.android.purchaseorder.presentation.uiState.CreateOrderState
import com.android.purchaseorder.presentation.viewModels.CreateOrderViewModel
import com.android.purchaseorder.utils.BitmapUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateOrderActivity : AppCompatActivity() {

    private lateinit var dialogOrderSummaryBinding: DialogOrderSummaryBinding
    private lateinit var binding: ActivityCreateOrderBinding

    private lateinit var productsAdapter: ProductListArrayAdapter
    private lateinit var quantityAdapter: ArrayAdapter<String>
    private lateinit var orderedProductListAdapter: OrderedProductListAdapter
    private lateinit var orderSummaryDialog: AlertDialog

    private var selectedProduct: Product? = null
    private var selectedQuantity: Int? = null

    private val createOrderViewModel: CreateOrderViewModel by viewModels()

    private val nameColWeight = 0.5f
    private val qtyColWeight = 0.15f
    private val amtColWeight = 0.35f

    private val RC_WRITE_STORAGE = 100
    private var showOKButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.create_order)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        setupAddToOrderAction()
        lifecycleScope.launchWhenCreated {
            createOrderViewModel.createOrderState
                .collect {
                    setCreateOrderState(it)
                }
        }
        createOrderViewModel.getAllProducts()
    }

    private fun setCreateOrderState(state: CreateOrderState) {
        when {
            state.isLoading -> {
                binding.tvError.gone()
                setValidDataUIViewsVisibility(false)
                binding.tvTotalOrderAmount.gone()
            }
            state.error.isNotEmpty() -> {
                onErrorState(state.error)
            }
            else -> {
                state.createOrderData?.also {
                    when {
                        state.createOrderData.productList.isEmpty() -> {
                            onErrorState(getString(R.string.add_products_to_create_order))
                        }
                        else -> {
                            showOKButton = true
                            setValidDataUIViewsVisibility(true)
                            setupProductSelectionAdapter(state.createOrderData.productList)
                            setupQuantitySelectionAdapter(state.createOrderData.maxQuantityLimit)
                            setupOrderedProductsAdapter()
                        }
                    }
                } ?: onErrorState(getString(R.string.err_failed_to_load_data))
            }
        }
        invalidateOptionsMenu()
    }

    private fun setValidDataUIViewsVisibility(show: Boolean) {
        binding.layoutSelectProduct.visibility = if (show) View.VISIBLE else View.GONE
        binding.layoutSelectQuantity.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnAddToOrder.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvOrderList.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun onErrorState(error: String) {
        binding.tvError.show()
        setValidDataUIViewsVisibility(false)
        binding.tvTotalOrderAmount.gone()

        binding.tvError.text = error
    }

    private fun setupProductSelectionAdapter(productList: List<Product>) {
        productsAdapter = ProductListArrayAdapter(this, productList)
        binding.tvSelectProduct.setAdapter(productsAdapter)
        binding.tvSelectProduct.setOnItemClickListener { adapterView, view, position, id ->
            val product = adapterView.getItemAtPosition(position) as? Product
            selectedProduct = product
            binding.tvSelectProduct.setText(product?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(it.name + "<br><font color='#777777'>₹${it.price}</font>",
                        Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(it.name + "<br><font color='#777777'>₹${it.price}</font>")
                }
            }, false)
        }
    }

    private fun setupQuantitySelectionAdapter(maxLimit: Int) {
        quantityAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, (1..maxLimit).map { it.toString() })
        binding.tvSelectQuantity.setAdapter(quantityAdapter)
        binding.tvSelectQuantity.setOnItemClickListener { adapterView, view, position, id ->
            val selectedQuantityStr = adapterView.getItemAtPosition(position) as? String
            if (!selectedQuantityStr.isNullOrEmpty())
                selectedQuantity = selectedQuantityStr.toInt()
        }
    }

    private fun setupAddToOrderAction() {
        binding.btnAddToOrder.setOnClickListener {
            when {
                selectedProduct == null -> {
                    showToast(R.string.select_product)
                }
                selectedQuantity == null -> {
                    showToast(R.string.select_quantity)
                }
                else -> {
                    orderedProductListAdapter.addOrderedProduct(
                        OrderedProduct(selectedProduct!!, selectedQuantity!!)
                    )
                    setTotalOrderAmount()
                }
            }
        }
    }

    private fun setupOrderedProductsAdapter() {
        orderedProductListAdapter = OrderedProductListAdapter(onDelete = { orderedProduct ->
            orderedProductListAdapter.deleteOrderedProduct(orderedProduct)
            setTotalOrderAmount()
        })
        binding.rvOrderList.layoutManager = LinearLayoutManager(this)
        binding.rvOrderList.setHasFixedSize(true)
        binding.rvOrderList.adapter = orderedProductListAdapter
    }

    private fun setTotalOrderAmount() {
        val shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setTopLeftCorner(CornerFamily.ROUNDED, 16f)
            .setTopRightCorner(CornerFamily.ROUNDED, 16f)
            .build()

        val background = MaterialShapeDrawable(shapeAppearanceModel).apply {
            setTint(ContextCompat.getColor(this@CreateOrderActivity,
                R.color.colorTotalOrderAmountBg))
            paintStyle = Paint.Style.FILL
        }

        ViewCompat.setBackground(binding.tvTotalOrderAmount, background)

        if (::orderedProductListAdapter.isInitialized) {
            if (orderedProductListAdapter.orderedProductList.isEmpty()) {
                binding.tvTotalOrderAmount.visibility = View.GONE
            } else {
                binding.tvTotalOrderAmount.visibility = View.VISIBLE
                orderedProductListAdapter.orderedProductList.sumOf { orderedProduct ->
                    orderedProduct.product.price * orderedProduct.quantity
                }.toString().also { totalOrderAmt ->
                    binding.tvTotalOrderAmount.text = "Total Amount : \u20B9$totalOrderAmt"
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_order_toolbar_menu, menu)
        menu?.findItem(R.id.mnuOK)?.isVisible = showOKButton
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.mnuOK -> {
                createOrder()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createOrder() {
        if (::orderedProductListAdapter.isInitialized.not() || orderedProductListAdapter.orderedProductList.isEmpty()) {
            showToast(R.string.add_products_to_create_order)
            return
        }
        createOrderViewModel.createOrder(Calendar.getInstance().time,
            orderedProductListAdapter.orderedProductList,
            orderedProductListAdapter.orderedProductList.sumOf { orderedProduct ->
                orderedProduct.product.price * orderedProduct.quantity
            })
        showOrderPreviewDialog(orderedProductListAdapter.orderedProductList)
    }

    private fun showOrderPreviewDialog(orderedProductList: MutableList<OrderedProduct>) {
        dialogOrderSummaryBinding = DialogOrderSummaryBinding.inflate(layoutInflater)
        dialogOrderSummaryBinding.tvOrderDate.text =
            Calendar.getInstance().time.toFormattedDate("dd MMM, yyyy")
        addHeaders(dialogOrderSummaryBinding)
        addData(dialogOrderSummaryBinding, orderedProductList)
        addFooter(dialogOrderSummaryBinding, orderedProductList)
        orderSummaryDialog = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setView(dialogOrderSummaryBinding.root)
            //.setTitle(R.string.order)
            .setPositiveButton(R.string.dialog_share, null)
            .setNegativeButton(R.string.dialog_close) { _, _ ->
                finish()
            }
            .show()
        orderSummaryDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
                ) {
                    if (ContextCompat.checkSelfPermission(this@CreateOrderActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    ) {
                        // Requesting the permission
                        ActivityCompat.requestPermissions(this@CreateOrderActivity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            RC_WRITE_STORAGE)
                    } else {
                        onShareClicked()
                    }
                } else {
                    onShareClicked()
                }
            }
    }

    private fun onShareClicked() {
        if (::dialogOrderSummaryBinding.isInitialized.not()) return
        val bitmap = dialogOrderSummaryBinding.root.drawToBitmap()
        val uri = BitmapUtils.saveImageToStorage(
            context = this@CreateOrderActivity,
            bitmap = bitmap,
            filename = "${
                SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",
                    Locale.getDefault()).format(Date())
            }_order.jpg"
        )
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg"
        }
        try {
            startActivity(Intent.createChooser(shareIntent,
                resources.getText(R.string.send_to)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        orderSummaryDialog.dismiss()
        finish()
    }

    private fun addHeaders(binding: DialogOrderSummaryBinding) {
        TableRow(this)
            .apply {
                layoutParams = getTableCellLayoutParams()
                this.background =
                    ContextCompat.getDrawable(this@CreateOrderActivity, R.drawable.table_row_bg)
                // NAME
                val nameTextView = getTextView(1000, getString(R.string.name))
                    .apply {
                        gravity = Gravity.START
                        typeface = Typeface.DEFAULT_BOLD
                    }
                addView(nameTextView)
                val nameTextLp = nameTextView.layoutParams as LayoutParams
                nameTextLp.weight = nameColWeight
                nameTextView.layoutParams = nameTextLp

                // QTY
                val qtyTextView = getTextView(2000, getString(R.string.quantity))
                    .apply {
                        gravity = Gravity.CENTER
                        typeface = Typeface.DEFAULT_BOLD
                    }
                addView(qtyTextView)
                val qtyTextLp = qtyTextView.layoutParams as LayoutParams
                qtyTextLp.weight = qtyColWeight
                qtyTextView.layoutParams = qtyTextLp

                // AMT
                val amtTextView = getTextView(3000, getString(R.string.amount))
                    .apply {
                        gravity = Gravity.END
                        typeface = Typeface.DEFAULT_BOLD
                    }
                addView(amtTextView)
                val amtTextLp = amtTextView.layoutParams as LayoutParams
                amtTextLp.weight = amtColWeight
                amtTextView.layoutParams = amtTextLp

                binding.tableOrder.addView(this, getTableLayoutParams())
            }
    }

    private fun addData(
        binding: DialogOrderSummaryBinding,
        orderedProductList: MutableList<OrderedProduct>,
    ) {
        val orderedProductSize: Int = orderedProductList.size
        for (index in 0 until orderedProductSize) {
            TableRow(this)
                .apply {
                    background = ContextCompat.getDrawable(this@CreateOrderActivity,
                        R.drawable.table_row_bg)
                    this.layoutParams = getTableCellLayoutParams()
                    // NAME
                    val nameTextView = getTextView(
                        id = index + 1,
                        text = orderedProductList[index].product.name).apply {
                    }.apply {
                        maxLines = 2
                        setTextColor(ContextCompat.getColor(this@CreateOrderActivity,
                            R.color.colorOrderSummaryContents))
                    }
                    addView(nameTextView)
                    val nameTextLp = nameTextView.layoutParams as LayoutParams
                    nameTextLp.weight = nameColWeight
                    nameTextView.layoutParams = nameTextLp

                    // QTY
                    val qtyTextView = getTextView(index + orderedProductSize,
                        orderedProductList[index].quantity.toString())
                        .apply {
                            gravity = Gravity.CENTER
                            setTextColor(ContextCompat.getColor(this@CreateOrderActivity,
                                R.color.colorOrderSummaryContents))
                        }
                    addView(qtyTextView)
                    val qtyTextLp = qtyTextView.layoutParams as LayoutParams
                    qtyTextLp.weight = qtyColWeight
                    qtyTextView.layoutParams = qtyTextLp

                    // AMT
                    val amtTextView = getTextView(index + (2 * orderedProductSize),
                        "${orderedProductList[index].quantity * orderedProductList[index].product.price}")
                        .apply {
                            gravity = Gravity.END
                            setTextColor(ContextCompat.getColor(this@CreateOrderActivity,
                                R.color.colorOrderSummaryContents))
                        }
                    addView(amtTextView)
                    val amtTextLp = amtTextView.layoutParams as LayoutParams
                    amtTextLp.weight = amtColWeight
                    amtTextView.layoutParams = amtTextLp

                    binding.tableOrder.addView(this, getTableLayoutParams())
                }
        }
    }

    private fun addFooter(
        binding: DialogOrderSummaryBinding,
        orderedProductList: MutableList<OrderedProduct>,
    ) {
        TableRow(this)
            .apply {
                this.layoutParams = getTableCellLayoutParams()
                this.background =
                    ContextCompat.getDrawable(this@CreateOrderActivity,
                        R.drawable.table_row_last_bg)
                val footerTextView = getTextView(4000,
                    "Grand Total : \u20B9${
                        orderedProductList.sumOf { orderedProduct ->
                            orderedProduct.product.price * orderedProduct.quantity
                        }
                    }")
                    .apply {
                        gravity = Gravity.END
                        typeface = Typeface.DEFAULT_BOLD
                    }
                addView(footerTextView)
                val footerTextLP = footerTextView.layoutParams as LayoutParams
                //footerTextLP.span = 3
                footerTextLP.weight = 1f
                footerTextView.layoutParams = footerTextLP
                binding.tableOrder.addView(this, getTableLayoutParams())
            }
    }

    private fun getTextView(id: Int, text: String) =
        TextView(this).apply {
            this.id = id
            this.text = text
            this.setTextColor(Color.BLACK)
            this.setPadding(8, 8, 8, 8)
            this.layoutParams = getTableCellLayoutParams()
        }

    private fun getTableCellLayoutParams() =
        LayoutParams(0, LayoutParams.WRAP_CONTENT)
            .apply {
                this.setMargins(0, 0, 0, 0)
            }

    private fun getTableLayoutParams() = TableLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode,
            permissions,
            grantResults)
        if (requestCode == RC_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showToast(R.string.storage_permissions_denied)
            } else {
                onShareClicked()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::orderSummaryDialog.isInitialized) {
            orderSummaryDialog.dismiss()
        }
    }
}