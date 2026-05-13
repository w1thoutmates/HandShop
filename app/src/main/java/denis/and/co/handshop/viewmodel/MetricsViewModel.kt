package denis.and.co.handshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.DailyReach
import denis.and.co.handshop.data.model.PriceComparisonData
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.utils.SimilarityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MetricsViewModel(
    private val sellerId: String,
    private val sellerRepo: SellerRepository
): ViewModel() {

    private val _reachPoints = MutableStateFlow<List<Point>>(emptyList())
    val reachPoints: StateFlow<List<Point>> = _reachPoints

    private val _stats = MutableStateFlow<List<DailyReach>>(emptyList())
    val stats: StateFlow<List<DailyReach>> = _stats

    private val _clickPoints = MutableStateFlow<List<Point>>(emptyList())
    val clickPoints: StateFlow<List<Point>> = _clickPoints

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    private val _ctrPoints = MutableStateFlow<List<Point>>(emptyList())
    val ctrPoints: StateFlow<List<Point>> = _ctrPoints

    private val _priceComparisonPoints = MutableStateFlow<PriceComparisonData?>(null)
    val priceComparisonData: StateFlow<PriceComparisonData?> = _priceComparisonPoints

    private val _removedFromLikedPoints = MutableStateFlow<List<Point>>(emptyList())
    val removedFromLikedPoints: StateFlow<List<Point>> = _removedFromLikedPoints

    private val _profileClickPoints = MutableStateFlow<List<Point>>(emptyList())
    val profileClickPoints: StateFlow<List<Point>> = _profileClickPoints

    init {
        loadStats()
    }

    fun loadStats(days: Int = 7) {
        viewModelScope.launch {
            val stats = sellerRepo.getDailyStats(sellerId, days)
            _stats.value = stats
            _reachPoints.value = mapToPoints(stats)
        }
    }

    private fun mapToPoints(
        stats: List<DailyReach>,
        isClicks: Boolean = false,
        isAddedToLiked: Boolean = false,
        isRemovedFromLiked: Boolean = false,
        productId: String? = null
    ): List<Point> {
        return stats.mapIndexed { index, stat ->
            Point(
                x = index.toFloat(),
                y = when {
                    isClicks -> {
                        stat.clicks.toFloat()
                    }
                    isAddedToLiked -> {
                        (stat.addedToLiked[productId] ?: 0L).toFloat()
                    }
                    isRemovedFromLiked -> {
                        (stat.removedFromLiked[productId] ?: 0L).toFloat()
                    }
                    else -> {
                        stat.impressions.toFloat()
                    }
                }
            )
        }
    }

    fun loadContactClicks(days: Int = 7) {
        viewModelScope.launch {
            val stats = sellerRepo.getContactClicksStats(sellerId, days)
            _stats.value = stats
            _clickPoints.value = mapToPoints(stats, true)
        }
    }

    fun loadProductsAndInitialStats(sellerId: String, days: Int = 7) {
        viewModelScope.launch {
            val allProducts = sellerRepo.getSellerProducts(sellerId)
            _products.value = allProducts

            val topProduct = allProducts.maxByOrNull { it.addedToLikedCount } ?: allProducts.firstOrNull()
            topProduct?.let {
                selectProduct(it, days)
            }
        }
    }

    fun loadProductsAndSelectLastPublished(sellerId: String, days: Int = 7) {
        viewModelScope.launch {
            val allProducts = sellerRepo.getSellerProducts(sellerId)

            _products.value = allProducts

            val lastPublishedProduct = allProducts
                .filter { it.status == ProductStatus.ACTIVE }
                .maxByOrNull { it.postedTime }

            if (lastPublishedProduct != null) {
                selectProductForPriceIndex(lastPublishedProduct, days)
            }
        }
    }

    fun selectProductForPriceIndex(product: Product, days: Int = 7) {
        _selectedProduct.value = product
        loadPriceIndexStats(product, days)
    }

    fun selectProduct(product: Product, days: Int = 7) {
        _selectedProduct.value = product
        viewModelScope.launch {
            val stats = sellerRepo.getProductDailyStats(product.sellerId, days)

            _stats.value = stats
            val addedPoints = mapToPoints(stats, isAddedToLiked = true, productId = product.id)
            val removedPoints = mapToPoints(stats, isRemovedFromLiked = true, productId = product.id)

            _reachPoints.value = addedPoints
            _removedFromLikedPoints.value = removedPoints
        }
    }

    fun updateProductClickStat(product: Product) {
        viewModelScope.launch {
            sellerRepo.updateProductClickStat(product.sellerId, product.id)
        }
    }

    fun selectProductForCTR(product: Product, days: Int = 7) {
        _selectedProduct.value = product
        viewModelScope.launch {
            val stats = sellerRepo.getProductDailyStats(product.sellerId, days)
            _stats.value = stats

            val points = stats.mapIndexed { index, stat ->
                val clicksOnThisProduct = stat.productClicks[product.id] ?: 0L

                val ctrValue = if (stat.impressions > 0) {
                    (clicksOnThisProduct.toFloat() / stat.impressions.toFloat()) * 100f
                } else {
                    0f
                }
                Point(x = index.toFloat(), y = ctrValue)
            }
            _reachPoints.value = points
        }
    }

    fun loadPriceIndexStats(selectedProduct: Product, days: Int = 7) {
        _selectedProduct.value = selectedProduct
        viewModelScope.launch {
            val categoryProducts = sellerRepo.getAllProductsByCategory(selectedProduct.category)

            val stats = sellerRepo.getProductDailyStats(selectedProduct.sellerId, days)
            _stats.value = stats

            val competitors = categoryProducts
                .filter { it.sellerId != selectedProduct.sellerId }
                .filter { SimilarityUtils.calculateSimilarity(selectedProduct.title, it.title) > 0.35 }

            val competitorCosts = competitors.mapNotNull { it.cost?.toFloat() }.sorted()

            val medianMarketPrice = if (competitorCosts.isNotEmpty()) {
                if (competitorCosts.size % 2 == 0) {
                    (competitorCosts[competitorCosts.size / 2] + competitorCosts[competitorCosts.size / 2 - 1]) / 2
                } else competitorCosts[competitorCosts.size / 2]
            } else {
                (selectedProduct.cost?.toFloat() ?: 0f) * 0.9f
            }

            val userPrices = stats.map { dayStat ->
                dayStat.productCosts[selectedProduct.id]?.toFloat() ?: selectedProduct.cost?.toFloat() ?: 0f
            }

            val marketPrices = List(stats.size) { medianMarketPrice }
            val dates = stats.map { it.date }

            _priceComparisonPoints.value = PriceComparisonData(userPrices, marketPrices, dates)
        }
    }

    fun updateProfileClicks(sellerId: String) {
        viewModelScope.launch {
            sellerRepo.updateProfileClicks(sellerId)
        }
    }

    fun loadProfileClicks(days: Int = 7) {
        viewModelScope.launch {
            val stats = sellerRepo.getDailyStats(sellerId, days)

            _stats.value = stats

            _profileClickPoints.value = stats.mapIndexed { index, stat ->
                Point(
                    x = index.toFloat(),
                    y = stat.profileClicks.toFloat()
                )
            }
        }
    }

}