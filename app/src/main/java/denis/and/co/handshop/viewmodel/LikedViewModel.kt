package denis.and.co.handshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.ProductWithSeller
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class LikedViewModel(
    private val productRepo: ProductRepository,
    private val sellerRepo: SellerRepository
): ViewModel() {

    private val _state = MutableStateFlow<CatalogState>(CatalogState.Loading)
    val state: StateFlow<CatalogState> = _state.asStateFlow()
    private val _likedProducts = MutableStateFlow<List<Product>>(emptyList())
    val likedProducts = _likedProducts.asStateFlow()

    val currentUserId = sellerRepo.getCurrentUserId()

    init {
        loadLikedProducts()
    }

    fun loadLikedProducts() {
        viewModelScope.launch {
            _state.value = CatalogState.Loading

            try {
                val userId = currentUserId ?: return@launch
                val sellerResult = sellerRepo.getSeller(userId).getOrNull()

                val likedIds = sellerResult?.likedProductIds ?: emptyList()

                if (likedIds.isEmpty()) {
                    _state.value = CatalogState.Empty
                    return@launch
                }

                val products = productRepo.getLikedProducts(likedIds)

                if (products.isEmpty()) {
                    _state.value = CatalogState.Empty
                    return@launch
                }

                val items = coroutineScope {
                    products.map { product ->
                        async {
                            val seller = sellerRepo.getSeller(product.sellerId)
                            ProductWithSeller(
                                product = product,
                                seller = seller.getOrNull()
                            )
                        }
                    }.awaitAll()
                }

                _state.value = CatalogState.Success(items)
            } catch (ex: Exception) {
                Log.e("VIEWMODEL_ERROR", "Ошибка в liked view model: ", ex)
                _state.value = CatalogState.Error(ex.message.toString())
            }
        }
    }

    fun addToLiked(productId: String) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                sellerRepo.addToLiked(userId = userId, productId = productId)
            } catch (ex: Exception) {
                Log.e("ADD_TO_LIKED_ERROR", "Ошибка добавления в избранное: ", ex)
            }
        }
    }

    fun deleteFromLiked(productId: String) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                sellerRepo.deleteFromLiked(userId = userId, productId = productId)
            } catch (ex: Exception) {
                Log.e("ADD_TO_LIKED_ERROR", "Ошибка удаления из избранного: ", ex)
            }
        }
    }

    fun searchInLiked(query: String) {

    }

    suspend fun isProductExistInLiked(productId: String): Boolean {
       return try {
           val userId = currentUserId ?: return false
           sellerRepo.isProductLikedBySellerId(userId, productId)
       } catch (ex: Exception) {
           Log.e("CHECK_LIKED_ERROR", "Ошибка проверки нахождения объявления в избранном: ", ex)
           false
       }
    }
}