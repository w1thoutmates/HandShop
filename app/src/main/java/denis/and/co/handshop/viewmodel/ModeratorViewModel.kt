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

sealed class ModeratorUiState {
    object Loading : ModeratorUiState()
    data class Success(val complaints: List<Complaint>) : ModeratorUiState()
    data class Error(val message: String) : ModeratorUiState()
    object Empty : ModeratorUiState()
}

class ModeratorViewModel(
    private val complaintRepo: ComplaintRepository,
    private val sellerRepo: SellerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ModeratorUiState>(ModeratorUiState.Loading)
    val uiState: StateFlow<ModeratorUiState> = _uiState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _targetSellerWarnings = MutableStateFlow<Map<String, Int>>(emptyMap())
    val targetSellerWarnings: StateFlow<Map<String, Int>> = _targetSellerWarnings.asStateFlow()

    init {
        loadComplaints()
    }

    fun loadComplaints() {
        viewModelScope.launch {
            _uiState.value = ModeratorUiState.Loading
            try {
                val complaints = complaintRepo.getPendingComplaints()
                if (complaints.isEmpty()) {
                    _uiState.value = ModeratorUiState.Empty
                } else {
                    _uiState.value = ModeratorUiState.Success(complaints)
                    loadWarningCounts(complaints.map { it.targetSellerId }.distinct())
                }
            } catch (ex: Exception) {
                _uiState.value = ModeratorUiState.Error("Ошибка загрузки жалоб: ${ex.message}")
            }
        }
    }

    private fun loadWarningCounts(sellerIds: List<String>) {
        viewModelScope.launch {
            val warningsMap = mutableMapOf<String, Int>()
            sellerIds.forEach { sellerId ->
                warningsMap[sellerId] = complaintRepo.getWarningsCount(sellerId)
            }
            _targetSellerWarnings.value = warningsMap
        }
    }

    fun warnSeller(targetSellerId: String, complaintId: String) {
        viewModelScope.launch {
            complaintRepo.warnSeller(targetSellerId, complaintId).onSuccess { newCount ->
                _actionMessage.value = "Предупреждение выдано ($newCount/3)"
                val updated = _targetSellerWarnings.value.toMutableMap()
                updated[targetSellerId] = newCount
                _targetSellerWarnings.value = updated
                removeComplaintFromList(complaintId)
            }.onFailure {
                _actionMessage.value = "Ошибка: ${it.message}"
            }
        }
    }

    fun banSeller(targetSellerId: String) {
        viewModelScope.launch {
            complaintRepo.banSeller(targetSellerId).onSuccess {
                _actionMessage.value = "Продавец заблокирован"
                loadComplaints()
            }.onFailure {
                _actionMessage.value = "Ошибка блокировки: ${it.message}"
            }
        }
    }

    fun rejectComplaint(complaintId: String) {
        viewModelScope.launch {
            complaintRepo.rejectComplaint(complaintId).onSuccess {
                _actionMessage.value = "Жалоба отклонена"
                removeComplaintFromList(complaintId)
            }.onFailure {
                _actionMessage.value = "Ошибка: ${it.message}"
            }
        }
    }

    fun ignoreReporter(reporterSellerId: String, complaintId: String) {
        viewModelScope.launch {
            complaintRepo.ignoreReporter(reporterSellerId, complaintId).onSuccess {
                _actionMessage.value = "Все жалобы от пользователя проигнорированы"
                loadComplaints()
            }.onFailure {
                _actionMessage.value = "Ошибка: ${it.message}"
            }
        }
    }

    fun clearAllResolved() {
        viewModelScope.launch {
            complaintRepo.clearResolvedComplaints().onSuccess {
                _actionMessage.value = "Обработанные жалобы удалены"
            }.onFailure {
                _actionMessage.value = "Ошибка очистки: ${it.message}"
            }
        }
    }

    fun clearMessage() {
        _actionMessage.value = null
    }

    private fun removeComplaintFromList(complaintId: String) {
        val current = _uiState.value
        if (current is ModeratorUiState.Success) {
            val updated = current.complaints.filter { it.id != complaintId }
            _uiState.value = if (updated.isEmpty()) ModeratorUiState.Empty else ModeratorUiState.Success(updated)
        }
    }
}