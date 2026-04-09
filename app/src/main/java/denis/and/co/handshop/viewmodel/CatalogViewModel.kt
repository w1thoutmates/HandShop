package denis.and.co.handshop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.ProductWithSeller
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.di.AppDependencies
import denis.and.co.handshop.di.AppDependencies.globalCatalogViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    init {
        globalCatalogViewModel = this
        loadRecommendations()
    }

    var scrollTrigger by mutableStateOf(0)

    fun refreshAndScroll() {
        scrollTrigger++
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

                val scoredItems = combinedList.map { product ->
                    val score = product.tags.sumOf { tag ->
                        tagStats[tag] ?: 0
                    } / product.tags.size.coerceAtLeast(1)
                    product to score
                }

                val sortedProducts = scoredItems
                    .sortedByDescending { it.second }
                    .map { it.first }

                val explorationRatio = 0.2f
                val explorationCount = (sortedProducts.size * explorationRatio).toInt().coerceAtLeast(1)

                val explorationItems = allActiveItems
                    .filter { product -> product.id !in sortedProducts.take(20).map { it.id } }
                    .shuffled()
                    .take(explorationCount)

                val finalList = mutableListOf<Product>()
                val explorationIterator = explorationItems.iterator()

                sortedProducts.forEachIndexed { index, product ->
                    finalList.add(product)

                    if (index % 4 == 3 && explorationIterator.hasNext()) {
                        finalList.add(explorationIterator.next())
                    }
                }

                while (explorationIterator.hasNext()) {
                    finalList.add(explorationIterator.next())
                }

                if (finalList.isEmpty()) {
                    _state.value = CatalogState.Empty
                } else {
                    val itemsWithSellers = finalList.map { product ->
                        val seller = AppDependencies.sellerRepository.getSeller(product.sellerId)
                        ProductWithSeller(product, seller.getOrNull())
                    }
                    _state.value = CatalogState.Success(itemsWithSellers)
                }
            } catch (ex: Exception) {
                _state.value = CatalogState.Error("Ошибка загрузки рекомендаций")
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _state.value = CatalogState.Loading
            try {
                val products = productRepo.searchProducts(query)

                if (products.isEmpty()) {
                    _state.value = CatalogState.Empty
                    return@launch
                }

                val items = coroutineScope {
                    products.map { product ->
                        async {
                            val seller = sellerRepo.getSeller(product.sellerId)
                            ProductWithSeller(product, seller.getOrNull())
                        }
                    }.awaitAll()
                }

                _state.value = CatalogState.Success(items)
            } catch (ex: Exception) {
                _state.value = CatalogState.Error("Ошибка поиска")
            }
        }
    }

    fun resetSearch() {
        _isSearching.value = false
    }

    fun updateProductViewsCount(productId: String) {
        viewModelScope.launch {
            productRepo.updateProductViewsCount(productId)
        }
    }

}