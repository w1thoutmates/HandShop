package denis.and.co.handshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import denis.and.co.handshop.data.model.Complaint
import denis.and.co.handshop.data.repository.ComplaintRepository
import denis.and.co.handshop.data.repository.SellerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ComplaintViewModel(
    private val complaintRepo: ComplaintRepository,
    private val sellerRepo: SellerRepository
) : ViewModel() {

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _sendResult = MutableStateFlow<String?>(null)
    val sendResult: StateFlow<String?> = _sendResult.asStateFlow()

    fun sendComplaint(targetSeller: denis.and.co.handshop.data.model.Seller, text: String) {
        viewModelScope.launch {
            _isSending.value = true
            val currentUserId = sellerRepo.getCurrentUserId()
            if (currentUserId == null) {
                _sendResult.value = "Ошибка: пользователь не авторизован"
                _isSending.value = false
                return@launch
            }

            val reporterProfile = sellerRepo.getSeller(currentUserId).getOrNull()

            val complaint = Complaint(
                reporterSellerId = currentUserId,
                reporterName = reporterProfile?.sellerName ?: "Аноним",
                reporterAvatar = reporterProfile?.profileImage ?: "",
                targetSellerId = targetSeller.id,
                targetSellerName = targetSeller.sellerName,
                targetSellerAvatar = targetSeller.profileImage,
                text = text
            )

            complaintRepo.sendComplaint(complaint).onSuccess {
                _sendResult.value = "Жалоба отправлена"
            }.onFailure {
                _sendResult.value = "Ошибка: ${it.message}"
            }

            _isSending.value = false
        }
    }

    fun clearResult() {
        _sendResult.value = null
    }
}