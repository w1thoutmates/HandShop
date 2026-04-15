package denis.and.co.handshop.data.model

data class Seller(
    val id: String = "",
    val rate: Double = 0.0,
    val reviewsCount: Int = 0,
    val realName: String = "",
    val sellerName: String = "",
    val description: String = "",
    val profileImage: String = "",
    val coverImageUrl: String = "",
    val workSamples: List<WorkSample> = emptyList(),
    val registrationDate: Long = System.currentTimeMillis(),
    val contacts: Map<String, String> = emptyMap(),
    val userTagStats: Map<String, Long> = emptyMap(),
    val ratingSum: Double = 0.0,
    val likedProductIds: List<String> = emptyList(),
    val selectedLocation: String = ""
) {
    init {
        require(rate in 0.0..5.0) { "Рейтинг не может быть ниже 0 и больше 5. Получен рейтинг: $rate" }
    }
}