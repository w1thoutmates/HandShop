package denis.and.co.handshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import denis.and.co.handshop.data.model.DailyReach
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

    init {
        loadStats()
    }

    fun loadStats(days: Int = 7) {
        viewModelScope.launch {
            val stats = sellerRepo.getDailyStats(sellerId, days)
            _reachPoints.value = mapToPoints(stats)
        }
    }

    suspend fun getStats(days: Int = 7): List<Point> {
        return mapToPoints(sellerRepo.getDailyStats(sellerId, days))
    }

    private fun mapToPoints(stats: List<DailyReach>): List<Point> {
        return stats.mapIndexed { index, reach ->
            Point(
                x = index.toFloat(),
                y = reach.impressions.toFloat()
            )
        }
    }

}