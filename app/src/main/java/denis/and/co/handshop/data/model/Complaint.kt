package denis.and.co.handshop.data.model

import denis.and.co.handshop.data.enums.ComplaintStatus

data class Complaint(
    val id: String = "",
    val reporterSellerId: String = "",
    val reporterName: String = "",
    val reporterAvatar: String = "",
    val targetSellerId: String = "",
    val targetSellerName: String = "",
    val targetSellerAvatar: String = "",
    val text: String = "",
    val date: Long = System.currentTimeMillis(),
    val status: ComplaintStatus = ComplaintStatus.PENDING
)