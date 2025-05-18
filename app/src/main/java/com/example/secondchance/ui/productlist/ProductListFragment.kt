package com.example.secondchance.ui.productlist

import android.util.Log

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import viewmodel.ProductViewModel
import com.example.secondchance.R
import com.example.secondchance.adapter.SellerAdapter
import com.example.secondchance.data.model.Product
import com.example.secondchance.databinding.FragmentProductListBinding
import viewmodel.SellerViewModel
import com.example.secondchance.adapter.ProductAdapter as ProductAdapter


class ProductListFragment : Fragment((R.layout.fragment_product_list)) {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter
    private lateinit var sellerAdapter: SellerAdapter

    private val sellerViewModel: SellerViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupRecyclerView()
        observeProducts()
        addDefaultProductsIfNeeded()
        sellerViewModel.fetchSellers()

        // new :
        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            sellerViewModel.sellers.observe(viewLifecycleOwner) { sellers ->

                val updatedSellers = sellers.map { seller ->
                    val sellerProducts = products.filter { it.sellerId == seller.sellerId }
                    seller.copy(products = sellerProducts)
                }

                sellerAdapter = SellerAdapter(
                    sellers = updatedSellers,
                    onProductClick = { product ->
                        val bundle = Bundle().apply {
                            putParcelable("product", product)
                        }
                        findNavController().navigate(
                            R.id.action_productListFragment_to_productDetailFragment,
                            bundle
                        )
                    },
                    onProductLongClick = { product ->
                        showOptionsDialog(product)
                    }
                )
                binding.rvSellers.adapter = sellerAdapter
            }
        }




        //old :
        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            productViewModel.sellerList.observe(viewLifecycleOwner) { sellers ->

                val updatedSellers = sellers.map { seller ->
                    val sellerProducts = products.filter { it.sellerId == seller.sellerId }
                    seller.copy(products = sellerProducts)
                }

                sellerAdapter = SellerAdapter(
                    sellers = updatedSellers,
                    onProductClick = { product ->
                        val bundle = Bundle().apply {
                            putParcelable("product", product)
                        }
                        findNavController().navigate(
                            R.id.action_productListFragment_to_productDetailFragment,
                            bundle
                        )
                    },
                    onProductLongClick = { product ->
                        showOptionsDialog(product)
                    }
                )
                binding.rvSellers.adapter = sellerAdapter
            }
        }

        binding.addButton.setOnClickListener {
            val action = ProductListFragmentDirections
                .actionProductListFragmentToAddEditProductFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun addDefaultProductsIfNeeded() {
        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                productViewModel.sellerList.observe(viewLifecycleOwner) { sellers ->
                    if (sellers.isNotEmpty()) {
                        val defaultProducts = listOf(
                            Product(
                                name = "Used Nespresso Machine",
                                price = "220 ₪",
                                description = "In great condition. Selling due to upgrade.",
                                imageRes = R.drawable.nespresso,
                                imageUri = null,
                                sellerId = sellers[0].sellerId
                            ),
                            Product(
                                name = "Dyson Cordless Vacuum",
                                price = "450 ₪",
                                description = "Works perfectly. Strong battery.",
                                imageRes = R.drawable.dyson,
                                imageUri = null,
                                sellerId = sellers[0].sellerId
                            ),
                            Product(
                                name = "Ergonomic Office Chair",
                                price = "180 ₪",
                                description = "Very comfortable. Selling due to space issues.",
                                imageRes = R.drawable.chair,
                                imageUri = null,
                                sellerId = sellers[0].sellerId
                            )
                        )

                        productViewModel.addDefaultProducts(defaultProducts)
                    }
                }
            }
        }
    }


    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onItemClick = { product ->
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                findNavController().navigate(R.id.action_productListFragment_to_productDetailFragment, bundle)
            },
            onItemLongClick = { product ->
                showDeleteDialog(product)
            }
        )
        binding.rvSellers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter


        }
    }



    private fun observeProducts() {
        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            if (products.isNullOrEmpty()) {
                Log.d("ProductListFragment", "No products found in database.")
            } else {
                Log.d("ProductListFragment", "Loaded ${products.size} products:")
                products.forEach { product ->
                    Log.d("ProductListFragment", "Product: ${product.name}, Price: ${product.price}")
                }
            }


            products?.let {
                productAdapter.submitList(products)
            }
        }
    }

    private fun showDeleteDialog(product: Product) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_product))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                productViewModel.deleteProduct(product)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun showOptionsDialog(product: Product) {
        val options = arrayOf("Edit", "Delete")

        AlertDialog.Builder(requireContext())
            .setTitle("Please Select")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val action = ProductListFragmentDirections
                            .actionProductListFragmentToAddEditProductFragment(product)
                        findNavController().navigate(action)

                    }
                    1 -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Delete") { dialog, _ ->
                                productViewModel.deleteProduct(product)
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
    }    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}