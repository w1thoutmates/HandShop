package denis.and.co.handshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import denis.and.co.handshop.data.model.Review
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.repository.ReviewRepository
import denis.and.co.handshop.data.repository.SellerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewsViewModel(
    private val sellerId: String,
    private val reviewsRepo: ReviewRepository,
    private val sellerRepo: SellerRepository
): ViewModel() {
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _seller = MutableStateFlow<Seller?>(null)
    val seller: StateFlow<Seller?> = _seller

    private val _ratingPoints = MutableStateFlow<List<Point>>(emptyList())
    val ratingPoints: StateFlow<List<Point>> = _ratingPoints

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val reviewsResult = reviewsRepo.getReviewsForSeller(sellerId)
            val sellerResult = sellerRepo.getSeller(sellerId).getOrNull()

            _reviews.value = reviewsResult
            _seller.value = sellerResult

            calculateRatingHistory(reviewsResult)
        }
    }

    suspend fun fixCurrentSellerRating() {
        reviewsRepo.recalculateSellerRating(sellerId).onSuccess { newRating ->
            Log.d("FIX", "Рейтинг исправлен на: $newRating")
        }
    }

    fun postReview(text: String, rate: Int) {
        val currentUserId = sellerRepo.getCurrentUserId() ?: return
        viewModelScope.launch {
            val reviewerProfile = sellerRepo.getSeller(currentUserId).getOrNull()
            val reviewerName = reviewerProfile?.sellerName ?: "Покупатель"
            val reviewerAvatar = reviewerProfile?.profileImage ?: ""

            val newReview = Review(
                sellerId = sellerId,
                text = text,
                selectedRate = rate,
                date = System.currentTimeMillis(),
                reviewerId = currentUserId,
                reviewerName = reviewerName,
                reviewerAvatar = reviewerAvatar
            )
            val result = reviewsRepo.addReviewAndUpdateSeller(newReview)
            if (result.isSuccess) {
                _reviews.value = listOf(newReview) + _reviews.value
                _seller.value = sellerRepo.getSeller(sellerId).getOrNull()
            }
        }
    }

    fun loadReviewsSortedByAsc() {
        viewModelScope.launch {
            val result = reviewsRepo.getReviewsSortedByAsc(sellerId)
            _reviews.value = result;
        }
    }

    fun loadReviewsSortedByDesc() {
        viewModelScope.launch {
            val result = reviewsRepo.getReviewsSortedByDesc(sellerId)
            _reviews.value = result;
        }
    }

    fun loadReviewsSortedByGreaterDate() {
        viewModelScope.launch {
            val result = reviewsRepo.getReviewsSortedByGreaterDate(sellerId)
            _reviews.value = result;
        }
    }

    private fun calculateRatingHistory(allReviews: List<Review>) {
        if (allReviews.isEmpty()) return

        val sortedReviews = allReviews.sortedBy { it.date }

        var currentSum = 0.0
        val points = sortedReviews.mapIndexed { index, review ->
            currentSum += review.selectedRate
            val averageAtThisPoint = currentSum / (index + 1)

            Point(
                x = index.toFloat(),
                y = averageAtThisPoint.toFloat()
            )
        }
        _ratingPoints.value = points
    }
}