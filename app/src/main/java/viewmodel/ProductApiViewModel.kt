package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondchance.R
import com.example.secondchance.data.model.Product
import com.example.secondchance.data.model.RetrofitInstance
import kotlinx.coroutines.launch

class ProductApiViewModel : ViewModel() {

    private val _apiProducts = MutableLiveData<List<Product>>() // או ApiProduct אם תעדיף
    val apiProducts: LiveData<List<Product>> get() = _apiProducts

    fun fetchProductsFromApi() {
        viewModelScope.launch {
            try {
                val products = RetrofitInstance.api.getAllProducts().map {
                    Product(
                        id = 0,
                        name = it.title,
                        description = it.description,
                        price = it.price.toString(),
                        imageUri = it.image,
                        imageRes = R.drawable.second_chance_def,
                        sellerId = "api"
                    )
                }
                _apiProducts.value = products
            } catch (e: Exception) {
                // טיפול בשגיאה
            }
        }
    }
}
