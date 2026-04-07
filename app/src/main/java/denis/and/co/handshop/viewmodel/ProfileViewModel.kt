package denis.and.co.handshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val sellerRepo: SellerRepository,
    private val productRepo: ProductRepository
): ViewModel() {
    private val _seller = MutableStateFlow<Seller?>(null)
    val seller = _seller.asStateFlow()

    private val _sellerProducts = MutableStateFlow<List<Product?>>(emptyList())
    val sellerProducts = _sellerProducts.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    val currentUid = auth.currentUser?.uid

    fun loadProfile(sellerId: String?) {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        val targetId = sellerId ?: currentUid ?: return

        val isMyProfile = targetId == currentUid

        viewModelScope.launch {
            val seller = sellerRepo.getSeller(targetId)
            _seller.value = seller.getOrNull()

            val products = productRepo.getProductsBySellerId(
                sellerId = targetId,
                onlyActive = !isMyProfile
            )
            _sellerProducts.value = products
        }
    }
}