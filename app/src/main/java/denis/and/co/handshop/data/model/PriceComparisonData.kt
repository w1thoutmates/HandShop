package denis.and.co.handshop.data.model

data class PriceComparisonData(
    val userPrices: List<Float>,
    val marketMedianPrices: List<Float>,
    val dates: List<String>
) {}
