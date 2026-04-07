package denis.and.co.handshop.data.model

data class WorkSample(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrls: List<String> = emptyList()
) {
}