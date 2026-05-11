package denis.and.co.handshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import denis.and.co.handshop.data.model.DailyReach
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.repository.SellerRepository
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

    suspend fun getStats(days: Int = 7): List<Point> {
        return mapToPoints(sellerRepo.getDailyStats(sellerId, days))
    }

    private fun mapToPoints(
        stats: List<DailyReach>,
        isClicks: Boolean = false,
        isAddedToLiked: Boolean = false,
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

    fun selectProduct(product: Product, days: Int = 7) {
        _selectedProduct.value = product
        viewModelScope.launch {
            val stats = sellerRepo.getProductDailyStats(product.sellerId, days)
            println("DEBUG: Загружено документов статистики: ${stats.size}")

            _stats.value = stats
            val newPoints = mapToPoints(stats, isAddedToLiked = true, productId = product.id)
            println("DEBUG: Сформировано точек для графика: ${newPoints.filter { it.y > 0 }.size} (с ненулевым значением)")

            _reachPoints.value = newPoints
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

}