package denis.and.co.handshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.ProductWithSeller
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository
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

    private var originalItems: List<ProductWithSeller> = emptyList()

    private var referenceIds: List<String> = emptyList()

    init {
        loadLikedProducts()
    }

    fun loadLikedProducts() {
        viewModelScope.launch {
            _state.value = CatalogState.Loading

            try {
                val userId = currentUserId ?: return@launch
                val sellerResult = sellerRepo.getSeller(userId).getOrNull()

                referenceIds = sellerResult?.likedProductIds ?: emptyList()

                if (referenceIds.isEmpty()) {
                    _state.value = CatalogState.Empty
                    return@launch
                }

                val products = productRepo.getLikedProducts(referenceIds)

                val sellerIds = products.map { it.sellerId }
                val sellersMap = sellerRepo.getSellersByIds(sellerIds)


                val items = products.map { product ->
                    ProductWithSeller(product, sellersMap[product.sellerId])
                }

                originalItems = items

                sortLikedProducts("Сначала новые")
            } catch (ex: Exception) {
                Log.e("VIEWMODEL_ERROR", "Ошибка в liked view model: ", ex)
                _state.value = CatalogState.Error(ex.message.toString())
            }
        }
    }

    fun addToLiked(productId: String, ownerId: String) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                sellerRepo.addToLiked(userId = userId, productId = productId, ownerId = ownerId)
            } catch (ex: Exception) {
                Log.e("ADD_TO_LIKED_ERROR", "Ошибка добавления в избранное: ", ex)
            }
        }
    }

    fun deleteFromLiked(productId: String, ownerId: String) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                sellerRepo.deleteFromLiked(userId = userId, productId = productId, ownerId = ownerId)

                loadLikedProducts()
            } catch (ex: Exception) {
                Log.e("DELETE_FROM_LIKED_ERROR", "Ошибка удаления: ", ex)
            }
        }
    }

    fun searchInLiked(query: String) {
        if (query.isBlank()) {
            _state.value = CatalogState.Success(originalItems)
            return
        }

        val filtered = originalItems.filter { item ->
            item.product.title.contains(query, ignoreCase = true) ||
                    item.product.description.contains(query, ignoreCase = true)
        }

        _state.value = if (filtered.isEmpty()) CatalogState.Empty else CatalogState.Success(filtered)
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

    fun sortLikedProducts(option: String) {
        val sortedList = when (option) {
            "Дороже" -> originalItems.sortedByDescending { it.product.cost }
            "Дешевле" -> originalItems.sortedBy { it.product.cost }
            "Сначала новые" -> {
                originalItems.sortedByDescending { item ->
                    referenceIds.indexOf(item.product.id)
                }
            }
            "Скрытые" -> originalItems.filter { it.product.status == ProductStatus.HIDDEN }
            "Проданные" -> originalItems.filter { it.product.status == ProductStatus.SOLD }
            "Только активные" -> originalItems.filter { it.product.status == ProductStatus.ACTIVE }
            else -> originalItems
        }

        _state.value = CatalogState.Success(sortedList)
    }
}