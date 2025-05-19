package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.secondchance.data.model.Seller
import com.example.secondchance.repository.SellerRepository

class SellerViewModel : ViewModel() {

    private val repository = SellerRepository()

    private val _sellers = MutableLiveData<List<Seller>>()
    val sellers: LiveData<List<Seller>> get() = _sellers

    fun fetchSellers() {
        repository.getAllSellers { sellerList ->
            _sellers.postValue(sellerList)
        }
    }

    fun addSeller(seller: Seller, onComplete: (Boolean) -> Unit) {
        repository.addSeller(seller) { success ->
            if (success) fetchSellers()
            onComplete(success)
        }
    }
}
