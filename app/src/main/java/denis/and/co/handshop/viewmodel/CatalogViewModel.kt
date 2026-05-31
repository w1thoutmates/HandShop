package denis.and.co.handshop.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.ProductWithSeller
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.repository.ProductRepository
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.data.AppDependencies.globalCatalogViewModel
import denis.and.co.handshop.utils.SimilarityUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import kotlin.collections.sorted

class CatalogViewModel(
    private val productRepo: ProductRepository,
    private val sellerRepo: SellerRepository
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val _state = MutableStateFlow<CatalogState>(CatalogState.Loading)
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _currentUser = MutableStateFlow<Seller?>(null)
    val currentUser: StateFlow<Seller?> = _currentUser.asStateFlow()

    private var allSortedItems: List<ProductWithSeller> = emptyList()

    private val _hasMore = MutableStateFlow(false)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val trackedImpressions = mutableSetOf<String>()

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

    fun loadProducts() {
        viewModelScope.launch {
            resetPagination()
            _state.value = CatalogState.Loading
            try {
                val products = productRepo.getProducts()
                if (products.isEmpty()) {
                    _state.value = CatalogState.SearchEmpty
                    return@launch
                }
                val items = coroutineScope {
                    products.map { product ->
                        async {
                            val seller = sellerRepo.getSeller(product.sellerId)
                            ProductWithSeller(product = product, seller = seller.getOrNull())
                        }
                    }.awaitAll()
                }
                applyFirstPage(items)
            } catch (ex: Exception) {
                _state.value = CatalogState.Error("Ошибка: ${ex.message}")
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            resetPagination()
            _isSearching.value = true
            _state.value = CatalogState.Loading
            try {
                if (query.isBlank()) {
                    loadRecommendations()
                    return@launch
                }
                val products = productRepo.searchProducts(query)
                if (products.isEmpty()) {
                    _state.value = CatalogState.SearchEmpty
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
                applyFirstPage(items)
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
            resetPagination()
            _isSearching.value = true
            _state.value = CatalogState.Loading
            try {
                val products = productRepo.getProductsByCategory(category)
                if (products.isEmpty()) {
                    _state.value = CatalogState.SearchEmpty
                    return@launch
                }
                val items = coroutineScope {
                    products.map { product ->
                        async {
                            val seller = sellerRepo.getSeller(product.sellerId)
                            ProductWithSeller(product = product, seller = seller.getOrNull())
                        }
                    }.awaitAll()
                }
                applyFirstPage(items)
            } catch (ex: Exception) {
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
            resetPagination()
            _state.value = CatalogState.Loading
            try {
                val currentUser = _currentUser.value
                val tagStats = currentUser?.userTagStats ?: emptyMap()

                val likedTagBoost = buildLikedTagBoost(currentUser?.likedProductIds ?: emptyList())
                val combinedTagStats = mergeTagStats(tagStats, likedTagBoost)

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
                            combinedTagStats[tag] ?: 0
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
                    repeat(4) { if (localIterator.hasNext()) finalList.add(localIterator.next()) }
                    if (globalIterator.hasNext()) finalList.add(globalIterator.next())
                }

                if (finalList.isEmpty()) finalList.addAll(sortedGlobal)

                val explorationRatio = 0.2f
                val explorationCount = (finalList.size * explorationRatio).toInt().coerceAtLeast(1)
                val localExploration = localItems.filter { it.id !in sortedLocal.take(20).map { it.id } }.shuffled()
                val globalExploration = globalItems.filter { it.id !in sortedGlobal.take(20).map { it.id } }.shuffled()
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
                while (explorationIterator.hasNext()) enrichedList.add(explorationIterator.next())

                if (enrichedList.isEmpty()) {
                    _state.value = CatalogState.SearchEmpty
                    return@launch
                }

                val sellerIds = enrichedList.map { it.sellerId }
                val sellersMap = sellerRepo.getSellersByIds(sellerIds)
                val itemsWithSellers = enrichedList.map { product ->
                    ProductWithSeller(product, sellersMap[product.sellerId])
                }

                applyFirstPage(itemsWithSellers)
            } catch (ex: Exception) {
                Log.e("CATALOG_VM", "Ошибка рекомендаций", ex)
                _state.value = CatalogState.Error("Ошибка загрузки рекомендаций")
            }
        }
    }

    fun loadMore() {
        if (_isLoadingMore.value || !_hasMore.value) return
        val currentItems = (_state.value as? CatalogState.Success)?.items ?: return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val nextStart = currentItems.size
            val nextEnd = minOf(nextStart + PAGE_SIZE, allSortedItems.size)
            if (nextStart >= allSortedItems.size) {
                _hasMore.value = false
                _isLoadingMore.value = false
                return@launch
            }
            val nextPage = allSortedItems.subList(nextStart, nextEnd)
            _state.value = CatalogState.Success(currentItems + nextPage)
            _hasMore.value = nextEnd < allSortedItems.size
            _isLoadingMore.value = false
        }
    }

    private fun applyFirstPage(items: List<ProductWithSeller>) {
        allSortedItems = items
        val firstPage = items.take(PAGE_SIZE)
        _state.value = CatalogState.Success(firstPage)
        _hasMore.value = items.size > PAGE_SIZE
    }

    private fun resetPagination() {
        allSortedItems = emptyList()
        _hasMore.value = false
        _isLoadingMore.value = false
    }


    private suspend fun buildLikedTagBoost(likedProductIds: List<String>): Map<String, Long> {
        if (likedProductIds.isEmpty()) return emptyMap()
        return try {
            val likedProducts = productRepo.getLikedProducts(likedProductIds.take(20))
            val tagBoost = mutableMapOf<String, Long>()
            likedProducts.forEach { product ->
                product.tags.forEach { tag ->
                    tagBoost[tag] = (tagBoost[tag] ?: 0L) + 3L
                }
            }
            tagBoost
        } catch (ex: Exception) {
            emptyMap()
        }
    }

    private fun mergeTagStats(base: Map<String, Long>, boost: Map<String, Long>): Map<String, Long> {
        if (boost.isEmpty()) return base
        val merged = base.toMutableMap()
        boost.forEach { (tag, value) ->
            merged[tag] = (merged[tag] ?: 0L) + value
        }
        return merged
    }

    fun registerImpressionOnSession(product: Product) {
        if (trackedImpressions.contains(product.id)) return
        viewModelScope.launch {
            trackedImpressions.add(product.id)
            productRepo.updateImpressionsCount(product.id)
            sellerRepo.updateImpressionWithCost(product.sellerId, product)
        }
    }

    suspend fun getCostRecommendation(inputProduct: Product): String {
        return try {
            val userCost = inputProduct.cost?.toDouble() ?: 0.0
            if (userCost <= 0) return "Недостаточно данных для оценки"

            val allProducts = productRepo.getProducts()
            val categoryProducts = allProducts.filter {
                it.category == inputProduct.category && it.id != inputProduct.id
            }

            if (categoryProducts.isEmpty()) return "Недостаточно данных для оценки"

            val scoredProducts = categoryProducts.map { candidate ->
                val titleSim = SimilarityUtils.calculateSimilarity(
                    inputProduct.title, candidate.title,
                    inputProduct.tags, candidate.tags
                )
                val descSim = SimilarityUtils.calculateSimilarity(
                    inputProduct.description, candidate.description,
                    emptyList(), emptyList()
                )
                candidate to (titleSim * 0.8 + descSim * 0.2)
            }.sortedByDescending { it.second }

            val productsForAnalysis = scoredProducts
                .filter { it.second > 0.15 }
                .take(15)
                .map { it.first }
                .ifEmpty { categoryProducts.take(15) }

            val costs = productsForAnalysis
                .mapNotNull { it.cost?.toDouble() }
                .filter { it > 0 }
                .sorted()

            if (costs.isEmpty()) return "Недостаточно данных для оценки"

            val medianCost = if (costs.size % 2 == 0) {
                (costs[costs.size / 2] + costs[costs.size / 2 - 1]) / 2.0
            } else {
                costs[costs.size / 2]
            }

            val threshold = 0.15
            when {
                userCost < medianCost * (1 - threshold) -> "Цена ниже рынка"
                userCost > medianCost * (1 + threshold) -> "Цена выше рынка"
                else -> "Средняя цена по рынку"
            }
        } catch (ex: Exception) {
            Log.e("PRICE_ANALYTICS", "Ошибка аналитики цены", ex)
            "Недостаточно данных для оценки"
        }
    }

    fun logAllProductsData() {
        productRepo.logAllProductsData()
    }
}