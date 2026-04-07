package denis.and.co.handshop.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.repository.ImageRepository
import denis.and.co.handshop.data.repository.SellerRepository
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val imageRepo: ImageRepository,
    private val repository: SellerRepository
) : ViewModel() {

    var sellerState by mutableStateOf<Seller?>(null)
    var isUploading by mutableStateOf(false)

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
}