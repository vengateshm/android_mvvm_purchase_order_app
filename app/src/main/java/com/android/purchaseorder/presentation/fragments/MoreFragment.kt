package com.android.purchaseorder.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.purchaseorder.R
import com.android.purchaseorder.common.*
import com.android.purchaseorder.databinding.DialogSetMaxQuantityBinding
import com.android.purchaseorder.databinding.FragmentMoreBinding
import com.android.purchaseorder.presentation.activities.ProductListActivity
import com.android.purchaseorder.presentation.viewModels.MoreViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoreFragment : Fragment() {
    companion object {
        fun newInstance() = MoreFragment()
    }

    private lateinit var viewModel: MoreViewModel

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MoreViewModel::class.java)

        setMaxQuantityLimit()

        binding.layoutViewProducts.setOnClickListener {
            Intent(context, ProductListActivity::class.java)
                .also {
                    startActivity(it)
                }
        }
        binding.layoutMaxQuantityLimit.setOnClickListener {
            showSetMaxQuantityDialog()
        }
    }

    private fun setMaxQuantityLimit() {
        lifecycleScope.launch(Dispatchers.IO) {
            context?.dataStore?.data?.map {
                it[MAX_QUANTITY_LIMIT] ?: MAX_QUANTITY_DEFAULT
            }?.collectLatest { maxQuantity ->
                withContext(Dispatchers.Main.immediate) {
                    binding.tvMaxQuantityLimit.text =
                        "${getString(R.string.max_qty_limit)} ($maxQuantity)"
                }
            }
        }
    }

    private fun showSetMaxQuantityDialog() {
        val binding = DialogSetMaxQuantityBinding.inflate(layoutInflater)
        val setMaxQuantityDialog = MaterialAlertDialogBuilder(requireActivity())
            .setView(binding.root)
            .setTitle(R.string.max_qty_limit)
            .setPositiveButton(R.string.dialog_add, null)
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
        setMaxQuantityDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                val maxQuantityLimit = binding.layoutMaxQuantity.editText?.text.toString()
                when {
                    maxQuantityLimit.isEmpty() -> {
                        binding.tetMaxQuantityLimit.error = getString(R.string.err_field_required)
                    }
                    maxQuantityLimit.toInt() !in MIN_QUANTITY..MAX_QUANTITY -> {
                        binding.tetMaxQuantityLimit.error = getString(R.string.err_quantity_range)
                    }
                    else -> {
                        viewLifecycleOwner.lifecycleScope.launch {
                            context?.dataStore?.edit {
                                it[MAX_QUANTITY_LIMIT] = maxQuantityLimit.toInt()
                            }
                        }
                        setMaxQuantityDialog.dismiss()
                    }
                }
            }
    }
}