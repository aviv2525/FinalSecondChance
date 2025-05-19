package viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondchance.R
import com.example.secondchance.data.model.Product
import com.example.secondchance.data.model.Seller
import com.example.secondchance.data.model.ApiProduct
import com.example.secondchance.data.model.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductApiViewModel : ViewModel() {

    private val _apiProducts = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> = _apiProducts

    private val _sellerList = MutableLiveData<List<Seller>>()
    val sellerList: LiveData<List<Seller>> = _sellerList

    private val _operationFinished = MutableLiveData<Boolean>()
    val operationFinished: LiveData<Boolean> get() = _operationFinished


    init {
        fetchProductsFromApi()
        loadDummySellers()
    }

    fun fetchProductsFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val products = RetrofitInstance.api.getAllProducts().map {
                    Product(
                        id = it.id,
                        name = it.title,
                        description = it.description,
                        price = it.price.toString(),
                        imageUri = it.image,
                        imageRes = R.drawable.second_chance_def,
                        sellerId = "api"
                    )
                }
                _apiProducts.postValue(products)
            } catch (e: Exception) {
                Log.e("API_TEST", "API error", e)
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newApiProduct = ApiProduct(
                    id = 0,
                    title = product.name,
                    price = product.price.toDoubleOrNull() ?: 0.0,
                    description = product.description,
                    category = "general",
                    image = product.imageUri ?: ""
                )
                val response = RetrofitInstance.api.addProduct(newApiProduct)
                if (response.isSuccessful) {
                    fetchProductsFromApi()
                    _operationFinished.postValue(true)

                } else {
                    Log.e("API_TEST", "Add failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "Add error", e)
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("API_TEST", "✏️ Updating product ID=${product.id}, name=${product.name}")
                val updatedApiProduct = ApiProduct(
                    id = product.id,
                    title = product.name,
                    price = product.price.replace(" ₪", "").toDoubleOrNull() ?: 0.0,
                    description = product.description,
                    category = "general",
                    image = product.imageUri ?: ""
                )
                val response = RetrofitInstance.api.updateProduct(product.id, updatedApiProduct)
                if (response.isSuccessful) {
                    _operationFinished.postValue(true)

                    Log.d("API_TEST", "✅ Product updated successfully.")
                    fetchProductsFromApi()
                } else {
                    Log.e("API_TEST", "❌ Failed to update product. Code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "🚨 Exception in updateProduct", e)
            }
        }
    }


    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.deleteProduct(product.id)
                if (response.isSuccessful) {
                    _apiProducts.postValue(_apiProducts.value?.filterNot { it.id == product.id })
                    Log.d("API_TEST", "Product deleted: ${product.id}")
                } else {
                    Log.e("API_TEST", "Failed to delete product: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "Delete error", e)
            }
        }
    }

    private fun loadDummySellers() {
        val dummySellers = listOf(
            Seller(
                sellerId = "api",
                name = "API Seller",
                phone = "",
                address = "",
                products = emptyList()
            )
        )
        _sellerList.value = dummySellers
    }

    fun addProductToSeller(sellerId: String, product: Product) {
        val currentList = _sellerList.value ?: return
        val newList = currentList.map { seller ->
            if (seller.sellerId == sellerId) {
                val updatedProducts = seller.products + product
                seller.copy(products = updatedProducts)
            } else seller
        }
        _sellerList.value = newList
        addProduct(product)
    }

    fun getSellerById(sellerId: String): Seller? {
        return sellerList.value?.find { it.sellerId == sellerId }
    }
}
