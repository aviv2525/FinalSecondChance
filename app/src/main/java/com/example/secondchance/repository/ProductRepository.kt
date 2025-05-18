package com.example.secondchance.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.secondchance.data.local.AppDatabase
import com.example.secondchance.data.local.ProductDao
import com.example.secondchance.data.model.Product
import com.example.secondchance.data.model.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductRepository(application: Application) {

    private val productsDao: ProductDao

    init {
        val db = AppDatabase.getDatabase(application)
        productsDao = db.ProductsDau()
    }

    fun getProducts(): LiveData<List<Product>> = productsDao.getProduct()

    fun addProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            productsDao.addProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            productsDao.deleteProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            productsDao.updateProduct(product)
        }
    }

//Product From the WEB
    suspend fun fetchProductsFromApi(): List<Product> {
        return try {
            val apiProducts = RetrofitInstance.api.getAllProducts()
            apiProducts.map { apiProduct ->
                Product(
                    id = 0, // השדה autoGenerate ב-Room
                    name = apiProduct.title,
                    description = apiProduct.description,
                    price = apiProduct.price.toString(),
                    imageUri = apiProduct.image,
                    sellerId = "api" // לדוגמה: "from_api"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}
