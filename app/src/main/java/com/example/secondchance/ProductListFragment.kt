package com.example.secondchance
import android.util.Log

import android.app.AlertDialog
import android.media.Image
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.secondchance.databinding.FragmentProductDetailBinding
import com.example.secondchance.databinding.FragmentProductListBinding
import com.example.secondchance.ProductAdapter as ProductAdapter


class ProductListFragment : Fragment((R.layout.fragment_product_list)) {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter
    private lateinit var sellerAdapter: SellerAdapter

    private lateinit var recyclerView: RecyclerView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)

        // כאן נקבל את ה-ViewModel
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getParcelable<Product>("product")


        setupRecyclerView()
        observeProducts()
        addDefaultProductsIfNeeded()
        //setupSellerRecyclerView() // חדש!

        //setupListeners()

        /*        productViewModel.sellerList.observe(viewLifecycleOwner) { sellers ->
                    sellerAdapter = SellerAdapter(sellers,
                        onProductClick = { product ->
                        val bundle = Bundle().apply {
                            putParcelable("product", product)
                        }
                        findNavController().navigate(R.id.action_productListFragment_to_productDetailFragment, bundle)
                    },
                        onProductLongClick = { product ->
                            // כאן תגדיר את פעולת ה-longClick (למשל מחיקה)
                            showDeleteDialog(product)
                        }
                    )
                    binding.rvSellers.adapter = sellerAdapter
                }*/

        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            productViewModel.sellerList.observe(viewLifecycleOwner) { sellers ->

                // בנה רשימת מוכרים חדשה עם המוצרים בפועל מתוך ה-DB
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
            findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragment)
        }


    }


    private fun addDefaultProductsIfNeeded() {
        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                productViewModel.sellerList.observe(viewLifecycleOwner) { sellers ->
                    if (sellers.isNotEmpty()) {
                        val defaultProducts = listOf(
                            Product(
                                name = "Nate Fucking Diaz!",
                                price = "100 ₪",
                                description = "אגדה ב-UFC",
                                imageRes = R.drawable.ic_launcher_background,
                                imageUri = null,
                                sellerId = sellers[0].sellerId // 👈 שיוך למוכר
                            ),
                            Product(
                                name = "No des",
                                price = "150 ₪",
                                description = "סתם מוצר מגניב",
                                imageRes = R.drawable.nate,
                                imageUri = null,
                                sellerId = sellers[0].sellerId
                            ),
                            Product(
                                name = "Product 3",
                                price = "200 ₪",
                                description = "עוד מוצר",
                                imageRes = R.drawable.ic_product,
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

    private fun setupSellerRecyclerView() {
        sellerAdapter = SellerAdapter(
            sellers = emptyList(),
            onProductClick = { product ->
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                findNavController().navigate(R.id.action_productListFragment_to_productDetailFragment, bundle)
            },
            onProductLongClick = { product ->
                showDeleteDialog(product)
            }
        )

        binding.rvSellers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sellerAdapter
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
        val options = arrayOf("ערוך", "מחק")

        AlertDialog.Builder(requireContext())
            .setTitle("בחר פעולה")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val bundle = Bundle().apply {
                            putParcelable("product", product)
                        }
                        findNavController().navigate(
                            R.id.action_productListFragment_to_addEditProductFragment,
                            bundle
                        )
                    }
                    1 -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage("אתה בטוח שברצונך למחוק את המוצר?")
                            .setPositiveButton("מחק") { dialog, _ ->
                                productViewModel.deleteProduct(product)
                                dialog.dismiss()
                            }
                            .setNegativeButton("ביטול") { dialog, _ ->
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