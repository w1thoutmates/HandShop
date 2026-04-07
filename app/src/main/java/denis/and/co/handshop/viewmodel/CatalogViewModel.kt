package denis.and.co.handshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.ProductWithSeller
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.di.AppDependencies
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class CatalogViewModel(
    private val productRepo: ProductRepository,
    private val sellerRepo: SellerRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CatalogState>(CatalogState.Loading)
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        loadRecommendations()
    }

    fun loadProducts(){
        viewModelScope.launch {
            _state.value = CatalogState.Loading
            try {
                val products = productRepo.getProducts()

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
            } catch (ex : Exception) {
                _state.value = CatalogState.Error("Ошибка: ${ex.message}")
            }
        }
    }

    fun loadRecommendations() {
        viewModelScope.launch {
            _state.value = CatalogState.Loading
            try {
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid

                val currentUser = currentUid?.let { AppDependencies.sellerRepository.getSeller(it) }
                val tagStats = currentUser?.getOrNull()?.userTagStats ?: emptyMap()

                val topTags = tagStats.entries
                    .sortedByDescending { it.value }
                    .take(10)
                    .map { it.key }

                val recommendedItems = if (topTags.isNotEmpty()) {
                    productRepo.getProductsByTags(topTags)
                } else {
                    emptyList()
                }

                val allActiveItems = productRepo.getProducts()

                val combinedList = (recommendedItems + allActiveItems).distinctBy { it.id }

                if (combinedList.isEmpty()) {
                    _state.value = CatalogState.Empty
                } else {
                    val itemsWithSellers = combinedList.map { product ->
                        val seller = AppDependencies.sellerRepository.getSeller(product.sellerId)
                        ProductWithSeller(product, seller.getOrNull())
                    }
                    _state.value = CatalogState.Success(itemsWithSellers)
                }
            } catch (e: Exception) {
                _state.value = CatalogState.Error("Ошибка загрузки рекомендаций")
            }
        }
    }


}