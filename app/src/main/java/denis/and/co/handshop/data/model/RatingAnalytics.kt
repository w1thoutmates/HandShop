package denis.and.co.handshop.data.model

data class RatingAnalytics(
    val ratio: Map<Int, Int>,
    val totalCount: Int,
    val averageRating: Double
) {
    fun getPercentageFor(stars: Int): Float {
        if (totalCount == 0) return 0f
        return (ratio[stars] ?: 0).toFloat() / totalCount
    }
}
