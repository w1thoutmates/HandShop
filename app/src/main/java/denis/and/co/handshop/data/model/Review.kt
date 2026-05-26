package denis.and.co.handshop.data.model

data class Review(
    val id: String = "",
    val sellerId: String? = null,
    val text: String = "",
    val selectedRate: Int = 0,
    // val imageUrls: List<String> = emptyList()
    val date: Long = System.currentTimeMillis(),
    val reviewerId: String = "",
    val reviewerName: String = "Аноним",
    val reviewerAvatar: String = ""
) {
    init {
        if(selectedRate != 0)
            require(selectedRate in 1..5) { "Рейтинг не может быть ниже 0 и больше 5. Получен рейтинг: $selectedRate" }
    }
}
