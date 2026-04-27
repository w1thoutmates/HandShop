package denis.and.co.handshop.data.model

import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.utils.toHexString

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
    val countClicksOnContacts: Long = 0,
    val likedProductIds: List<String> = emptyList(),
    val selectedLocation: String = "",
    val selfProfileTextColor: String = BlackText.toHexString(),
    val selfProfileBackground: String = SoftBack.toHexString(),
    val selfProfileFooterColor: String = HardBack.toHexString(),
    val selfProfileAccentColor: String = Accent.toHexString(),
    val selfProfileAccentTextColor: String = WhiteText.toHexString(),
    val selfProfileIconsColor: String = BlackText.toHexString()
) {
    init {
        require(rate in 0.0..5.0) { "Рейтинг не может быть ниже 0 и больше 5. Получен рейтинг: $rate" }
    }
}