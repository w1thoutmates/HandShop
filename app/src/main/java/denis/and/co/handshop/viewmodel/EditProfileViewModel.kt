package denis.and.co.handshop.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.repository.ImageRepository
import denis.and.co.handshop.data.repository.SellerRepository
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.utils.toHexString
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val imageRepo: ImageRepository,
    private val repository: SellerRepository
) : ViewModel() {

    var sellerState by mutableStateOf<Seller?>(null)
    var isUploading by mutableStateOf(false)

    var selfProfileTextColor by mutableStateOf(BlackText.toHexString())
        private set
    var selfProfileBackground by mutableStateOf(SoftBack.toHexString())
        private set
    var selfProfileFooterColor by mutableStateOf(HardBack.toHexString())
        private set
    var selfProfileAccentColor by mutableStateOf(Accent.toHexString())
        private set
    var selfProfileAccentTextColor by mutableStateOf(WhiteText.toHexString())
        private set
    var selfProfileIconsColor by mutableStateOf(BlackText.toHexString())
        private set

    val imageRepository = imageRepo;

    fun setSelfProfileBackgroundColor(color: Color) {
        selfProfileBackground = color.toHexString()
    }
    fun setSelfProfileTextColor(color: Color) {
        selfProfileTextColor = color.toHexString()
    }
    fun setSelfProfileFooterColor(color: Color) {
        selfProfileFooterColor = color.toHexString()
    }
    fun setSelfProfileAccentColor(color: Color) {
        selfProfileAccentColor = color.toHexString()
    }
    fun setSelfProfileAccentTextColor(color: Color) {
        selfProfileAccentTextColor = color.toHexString()
    }
    fun setSelfProfileIconsColor(color: Color) {
        selfProfileIconsColor = color.toHexString()
    }

    fun saveProfile(
        seller: Seller,
        localAvatar: Uri?,
        localCover: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isUploading = true

            val finalAvatarUrl = localAvatar?.let { imageRepo.uploadImage(it) } ?: seller.profileImage

            val finalCoverUrl = localCover?.let { imageRepo.uploadImage(it) } ?: seller.coverImageUrl

            val finalSeller = seller.copy(
                id = seller.id.ifEmpty { java.util.UUID.randomUUID().toString() },
                profileImage = finalAvatarUrl ?: "",
                coverImageUrl = finalCoverUrl ?: ""
            )

            repository.saveSeller(finalSeller).onSuccess {
                isUploading = false
                onSuccess()
            }.onFailure {
                isUploading = false
                // вывести ошибку
            }
        }
    }

    fun loadProfile(uid: String) {
        viewModelScope.launch {
            repository.getSeller(uid).onSuccess { loadedSeller ->
                sellerState = loadedSeller
            }
        }
    }

    fun initColorsFromSeller(seller: Seller?) {
        seller ?: return

        selfProfileBackground = seller.selfProfileBackground ?: selfProfileBackground
        selfProfileTextColor = seller.selfProfileTextColor ?: selfProfileTextColor
        selfProfileFooterColor = seller.selfProfileFooterColor ?: selfProfileFooterColor
        selfProfileAccentColor = seller.selfProfileAccentColor ?: selfProfileAccentColor
        selfProfileAccentTextColor = seller.selfProfileAccentTextColor ?: selfProfileAccentTextColor
        selfProfileIconsColor = seller.selfProfileIconsColor ?: selfProfileIconsColor
    }
}