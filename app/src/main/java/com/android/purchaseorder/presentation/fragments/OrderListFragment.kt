package com.android.purchaseorder.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.purchaseorder.R
import com.android.purchaseorder.databinding.FragmentOrderListBinding
import com.android.purchaseorder.domain.model.Order
import com.android.purchaseorder.presentation.activities.CreateOrderActivity
import com.android.purchaseorder.presentation.adapters.OrderWithProductsListAdapter
import com.android.purchaseorder.presentation.uiState.OrderWithProductListState
import com.android.purchaseorder.presentation.viewModels.OrderListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OrderListFragment : Fragment() {

    companion object {
        fun newInstance() = OrderListFragment()
    }

    private lateinit var viewModel: OrderListViewModel
    private lateinit var orderWithProductsListAdapter: OrderWithProductsListAdapter

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderListViewModel::class.java)

        setupOrderWithProductsAdapter()
        setupCreateOrderFab()

        lifecycleScope.launchWhenResumed {
            viewModel.orderWithProductListState.collect { orderWithProductListState ->
                setOrderWithProductListState(orderWithProductListState)
            }
        }
    }

    private fun setupCreateOrderFab() {
        binding.fabCreateOrder.setOnClickListener {
            Intent(context, CreateOrderActivity::class.java)
                .also {
                    startActivity(it)
                }
        }
    }

    private fun setOrderWithProductListState(orderWithProductListState: OrderWithProductListState) {
        when {
            orderWithProductListState.isLoading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE
                binding.rvOrderWithProductList.visibility = View.GONE
            }
            orderWithProductListState.error.isNotBlank() -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = orderWithProductListState.error
                binding.rvOrderWithProductList.visibility = View.GONE
            }
            else -> {
                binding.progressBar.visibility = View.GONE
                if (orderWithProductListState.orderWithProductList.isEmpty()) {
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = getString(R.string.no_orders_created)
                    binding.rvOrderWithProductList.visibility = View.GONE
                } else {
                    binding.tvError.visibility = View.GONE
                    binding.rvOrderWithProductList.visibility = View.VISIBLE
                    orderWithProductsListAdapter.setOrderWithProducts(orderWithProductListState.orderWithProductList)
                }
            }
        }
    }

    private fun setupOrderWithProductsAdapter() {
        orderWithProductsListAdapter = OrderWithProductsListAdapter(this::onDeleteOrderClicked)
        binding.rvOrderWithProductList.layoutManager = LinearLayoutManager(context)
        binding.rvOrderWithProductList.adapter = orderWithProductsListAdapter
        binding.rvOrderWithProductList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.fabCreateOrder.visibility = View.GONE
                } else if (dy < 0) {
                    binding.fabCreateOrder.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun onDeleteOrderClicked(order: Order) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete this order?")
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteOrder(order)
            }
            .setNegativeButton(R.string.dialog_close) { _, _ ->
            }
            .show()
    }
}