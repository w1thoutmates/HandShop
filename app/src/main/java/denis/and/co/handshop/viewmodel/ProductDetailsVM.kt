package denis.and.co.handshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailsVM(
    private val productRepo: ProductRepository,
    private val sellerRepo: SellerRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    private val _seller = MutableStateFlow<Seller?>(null)
    val seller: StateFlow<Seller?> = _seller.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            val result = productRepo.getProductById(productId)
            val product = result.getOrNull()
            _product.value = product

            if (product != null) {
                val sellerResult = sellerRepo.getSeller(product.sellerId)
                _seller.value = sellerResult.getOrNull()

                val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUid != null) {
                    productRepo.updateTagStats(currentUid, product.tags)
                }
            }
        }
    }

    private suspend fun loadSeller(sellerId: String) {
        sellerRepo.getSeller(sellerId).onSuccess { loadedSeller ->
            _seller.value = loadedSeller
        }
    }
}