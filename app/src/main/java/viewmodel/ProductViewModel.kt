package viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.secondchance.R
import com.example.secondchance.data.model.Product
import com.example.secondchance.data.model.Seller
import com.example.secondchance.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = ProductRepository(application)
    val productList: LiveData<List<Product>> = repository.getProducts()

    private val _sellerList = MutableLiveData<List<Seller>>()
    val sellerList: LiveData<List<Seller>> = _sellerList

    init {
        loadDummySellers()
    }

    private fun addProduct(product: Product){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(product)
        }
    }

    fun addDefaultProducts(products: List<Product>) {
        for (product in products) {
            addProduct(product)
        }
    }
   /*
   הערות לערן !
    כאן יש את רשימת המוכרים שסידרנו שתהיה להגשה
     כרגע 2 רק 2 מוכרים וניתן להעביר מוצרים בינהם
    בפועל צריך להיות כפתור להוסיף מוכרים ואת זה שמרנו להמשך :)

    */

    private fun loadDummySellers() {
        val dummySellers = listOf(
            Seller(
                sellerId = "1",
                name = "Seller 1",
                phone = "050-1234567",
                address = "10 Example Street, Tel Aviv",
                products = listOf(
                    Product(
                        name = "Bluetooth Speaker",
                        description = "Portable, used a few times, works perfectly.",
                        price = "90 ₪",
                        imageRes = R.drawable.ic_launcher_foreground,
                        imageUri = null,
                        sellerId = "1"
                    ),
                    Product(
                        name = "Laptop Stand",
                        description = "Adjustable aluminum stand for laptops.",
                        price = "60 ₪",
                        imageRes = R.drawable.ic_launcher_foreground,
                        imageUri = null,
                        sellerId = "1"
                    )
                )
            ),
            Seller(
                sellerId = "2",
                name = "Seller 2",
                phone = "050-7654321",
                address = "20 Demo Avenue, Jerusalem",
                products = listOf(
                    Product(
                        name = "Desk Lamp",
                        description = "LED lamp with 3 brightness modes.",
                        price = "50 ₪",
                        imageRes = R.drawable.ic_launcher_foreground,
                        imageUri = null,
                        sellerId = "2"
                    ),
                    Product(
                        name = "Wall Clock",
                        description = "Minimalist design, works great.",
                        price = "40 ₪",
                        imageRes = R.drawable.ic_launcher_foreground,
                        imageUri = null,
                        sellerId = "2"
                    )
                )
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

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }


}
