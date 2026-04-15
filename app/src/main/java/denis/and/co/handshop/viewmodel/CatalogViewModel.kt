package denis.and.co.handshop.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.ProductWithSeller
import denis.and.co.handshop.data.model.Seller
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

    private val _currentUser = MutableStateFlow<Seller?>(null)
    val currentUser: StateFlow<Seller?> = _currentUser.asStateFlow()

    init {
        globalCatalogViewModel = this
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val uid = sellerRepo.getCurrentUserId()
                if (uid != null) {
                    val result = sellerRepo.getSeller(uid)
                    _currentUser.value = result.getOrNull()

                    loadRecommendations()
                } else {
                    loadRecommendations()
                }
            } catch (ex: Exception) {
                Log.e("CATALOG_VM", "Ошибка загрузки профиля пользователя", ex)

                loadRecommendations()
            }
        }
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

    fun loadRecommendationsWithoutFilteredByLocation() {
        viewModelScope.launch {
            _state.value = CatalogState.Loading
            try {
                val currentUser = _currentUser.value
                val tagStats = currentUser?.userTagStats ?: emptyMap()
                val selectedLocation = currentUser?.selectedLocation ?: "выбрать город"

                val topTags = tagStats.entries
                    .sortedByDescending { it.value }
                    .take(10)
                    .map { it.key }

                val recommendedItems = if (topTags.isNotEmpty()) {
                    productRepo.getProductsByTags(topTags)
                        .filter { product ->
                            selectedLocation.isEmpty() ||
                            selectedLocation == "выбрать город" ||
                            product.targetCity == selectedLocation
                        }
                } else {
                    emptyList()
                }

                val allActiveItems = productRepo.getProducts()
                    .filter { product ->
                        selectedLocation.isEmpty() ||
                        selectedLocation == "выбрать город" ||
                        product.targetCity == selectedLocation
                    }

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
                    val sellerIds = finalList.map { it.sellerId }
                    val sellersMap = sellerRepo.getSellersByIds(sellerIds)

                    val itemsWithSellers = finalList.map { product ->
                        ProductWithSeller(product, sellersMap[product.sellerId])
                    }
                    _state.value = CatalogState.Success(itemsWithSellers)
                }
            } catch (ex: Exception) {
                Log.e("CATALOG_VM", "Ошибка рекомендаций", ex)
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

    fun loadProductsByCategory(category: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _state.value = CatalogState.Loading
            try {
                val products = productRepo.getProductsByCategory(category)

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
                _state.value = CatalogState.Error("Ошибка загрузки объявлений по категории \"$category\": ${ex.message}")
            }
        }
    }

    fun updateSelectedLocation(location: String) {
        viewModelScope.launch {
            try {
                val userId = sellerRepo.getCurrentUserId() ?: return@launch
                sellerRepo.updateSelectedLocation(userId = userId, location = location)

                _currentUser.value = _currentUser.value?.copy(selectedLocation = location)

                loadRecommendations()
            } catch (ex: Exception) {
                Log.e("UPDATE_SELECTED_LOCATION_ERROR", "Ошибка обновления города: ", ex)
            }
        }
    }

    fun loadRecommendations() {
        viewModelScope.launch {
            _state.value = CatalogState.Loading
            try {
                val currentUser = _currentUser.value
                val tagStats = currentUser?.userTagStats ?: emptyMap()
                val selectedLocation = currentUser?.selectedLocation ?: ""

                val allActiveItems = productRepo.getProducts()

                val localItems = if (selectedLocation.isBlank() || selectedLocation == "выбрать город") {
                    emptyList()
                } else {
                    allActiveItems.filter { it.targetCity == selectedLocation }
                }

                val globalItems = if (localItems.isEmpty()) {
                    allActiveItems
                } else {
                    allActiveItems.filter { it.targetCity != selectedLocation }
                }

                fun scoreProducts(products: List<Product>): List<Product> {
                    return products.map { product ->
                        val score = product.tags.sumOf { tag ->
                            tagStats[tag] ?: 0
                        } / product.tags.size.coerceAtLeast(1)

                        product to score
                    }
                        .sortedByDescending { it.second }
                        .map { it.first }
                }

                val sortedLocal = scoreProducts(localItems)
                val sortedGlobal = scoreProducts(globalItems)

                val finalList = mutableListOf<Product>()

                val localIterator = sortedLocal.iterator()
                val globalIterator = sortedGlobal.iterator()

                while (localIterator.hasNext()) {
                    repeat(4) {
                        if (localIterator.hasNext()) {
                            finalList.add(localIterator.next())
                        }
                    }

                    if (globalIterator.hasNext()) {
                        finalList.add(globalIterator.next())
                    }
                }

                if (finalList.isEmpty()) {
                    finalList.addAll(sortedGlobal)
                }

                val explorationRatio = 0.2f
                val explorationCount = (finalList.size * explorationRatio)
                    .toInt()
                    .coerceAtLeast(1)

                val localExploration = localItems
                    .filter { it.id !in sortedLocal.take(20).map { it.id } }
                    .shuffled()

                val globalExploration = globalItems
                    .filter { it.id !in sortedGlobal.take(20).map { it.id } }
                    .shuffled()

                val explorationItems = (
                        localExploration.take(explorationCount / 2) +
                                globalExploration.take(explorationCount / 2)
                        ).shuffled()

                val explorationIterator = explorationItems.iterator()
                val enrichedList = mutableListOf<Product>()

                finalList.forEachIndexed { index, product ->
                    enrichedList.add(product)

                    if (index % 5 == 4 && explorationIterator.hasNext()) {
                        enrichedList.add(explorationIterator.next())
                    }
                }

                while (explorationIterator.hasNext()) {
                    enrichedList.add(explorationIterator.next())
                }

                if (enrichedList.isEmpty()) {
                    _state.value = CatalogState.Empty
                } else {
                    val sellerIds = enrichedList.map { it.sellerId }
                    val sellersMap = sellerRepo.getSellersByIds(sellerIds)

                    val itemsWithSellers = enrichedList.map { product ->
                        ProductWithSeller(product, sellersMap[product.sellerId])
                    }

                    _state.value = CatalogState.Success(itemsWithSellers)
                }

            } catch (ex: Exception) {
                Log.e("CATALOG_VM", "Ошибка рекомендаций", ex)
                _state.value = CatalogState.Error("Ошибка загрузки рекомендаций")
            }
        }
    }

}