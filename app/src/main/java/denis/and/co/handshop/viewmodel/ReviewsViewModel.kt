package denis.and.co.handshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import denis.and.co.handshop.R
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

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val reviewsResult = reviewsRepo.getReviewsForSeller(sellerId)
            val sellerResult = sellerRepo.getSeller(sellerId).getOrNull()

            _reviews.value = reviewsResult
            _seller.value = sellerResult
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
}