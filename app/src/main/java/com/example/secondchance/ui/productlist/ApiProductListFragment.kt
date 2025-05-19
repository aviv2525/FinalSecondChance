package com.example.secondchance.ui.productlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.secondchance.R
import com.example.secondchance.adapter.ProductAdapter
import com.example.secondchance.data.model.Product
import com.example.secondchance.databinding.FragmentApiProductListBinding
import viewmodel.ProductApiViewModel

class ApiProductListFragment : Fragment() {

    private var _binding: FragmentApiProductListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductApiViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProducts()

        viewModel.fetchProductsFromApi()

        binding.btnAddProduct.setOnClickListener {
            val action = ApiProductListFragmentDirections
                .actionApiProductListFragmentToAddEditProductFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            onItemClick = { product ->
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                findNavController().navigate(
                    R.id.action_apiProductListFragment_to_productDetailFragment,
                    bundle
                )
            },
            onItemLongClick = { product ->
                showOptionsDialog(product)
            }
        )
        binding.rvApiProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApiProducts.adapter = adapter
    }

    private fun observeProducts() {
        viewModel.productList.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
    }

    private fun showOptionsDialog(product: Product) {
        val options = arrayOf("Edit", "Delete")

        AlertDialog.Builder(requireContext())
            .setTitle("Please Select")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val action = ApiProductListFragmentDirections
                            .actionApiProductListFragmentToAddEditProductFragment(product)
                        findNavController().navigate(action)
                    }
                    1 -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Delete") { dialog, _ ->
                                viewModel.deleteProduct(product)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
