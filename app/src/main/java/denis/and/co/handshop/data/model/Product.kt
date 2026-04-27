package denis.and.co.handshop.data.model

import denis.and.co.handshop.data.enums.ProductStatus
import com.fasterxml.uuid.Generators

data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrls: List<String> = emptyList(),
    val cost: Long? = null,
    val currency: String? = "₽",
    val viewsCount: Long = 0,
    val impressionsCount: Long = 0,
    val postedTime: Long = System.currentTimeMillis(),
    val targetCity: String = "",
    val category: String = "",
    val sellerId: String = "",
    val status: ProductStatus = ProductStatus.ACTIVE,
    val addedToLikedCount: Long = 0,
    val tags: List<String> = emptyList(),
    val searchIndex: List<String> = emptyList()
) { }