package com.example.secondchance.ui.productlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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

        // Fetch on first load
        viewModel.fetchProductsFromApi()

        binding.btnRefreshApi.setOnClickListener {
            viewModel.fetchProductsFromApi()
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            onItemClick = { product ->
                // אפשר לנווט כאן לעמוד פרטי מוצר אם רוצים
            },
            onItemLongClick = { product ->
                // אפשר להוסיף למועדפים כאן אם רוצים
            }
        )
        binding.rvApiProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApiProducts.adapter = adapter
    }

    private fun observeProducts() {
        viewModel.apiProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
