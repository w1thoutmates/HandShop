package denis.and.co.handshop.data.model

data class DailyReach(
    var date: String = "",
    var impressions: Long = 0,
    var clicks: Long = 0,
    var addedToLiked: Map<String, Long> = emptyMap(),
    var removedFromLiked: Map<String, Long> = emptyMap(),
    var productClicks: Map<String, Long> = emptyMap(),
    val productCosts: Map<String, Long> = emptyMap()
)