package denis.and.co.handshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            productRepo.getProductById(productId).onSuccess { loadedProduct ->
                _product.value = loadedProduct
                if(loadedProduct?.sellerId != null)
                    loadSeller(loadedProduct.sellerId)
            }
        }
    }

    private suspend fun loadSeller(sellerId: String) {
        sellerRepo.getSeller(sellerId).onSuccess { loadedSeller ->
            _seller.value = loadedSeller
        }
    }
}