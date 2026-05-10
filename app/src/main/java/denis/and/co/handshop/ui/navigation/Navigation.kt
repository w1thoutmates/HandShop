package denis.and.co.handshop.ui.navigation

import denis.and.co.handshop.data.model.Seller
import kotlinx.serialization.Serializable

@Serializable
object RecommendationRoute : AppRoute

@Serializable
object SearchByCategoryRoute : AppRoute

@Serializable
data class ProductDetailsRoute(val productId: String) : AppRoute

@Serializable
object CreateProductRoute : AppRoute

@Serializable
data class ProfileRoute(val sellerId: String? = null) : AppRoute

@Serializable
object LikedRoute : AppRoute

@Serializable
data class EditProductRoute(val productId: String) : AppRoute

@Serializable
object LoginRoute : AppRoute

@Serializable
object CreateProfileRoute : AppRoute

@Serializable
object EditProfileRoute : AppRoute

@Serializable
data class ReviewsRoute(val sellerId: String) : AppRoute

@Serializable
data class MetricsRoute(val sellerId: String) : AppRoute

@Serializable
data class SellerRateMetricRoute(val sellerId: String) : AppRoute

@Serializable
object TotalReachMetricRoute : AppRoute

@Serializable
object ProductCategoryRationMetricRoute : AppRoute

@Serializable
data class AddedToLikedMetricRoute(val sellerId: String) : AppRoute

@Serializable
object CTRMetricRoute : AppRoute

@Serializable
object CompetitorsCostCompareMetricRoute : AppRoute

@Serializable
object ClicksOnContactsMetricRoute : AppRoute

@Serializable
data class ExpandedPublishedProductsRoute(val sellerId: String) : AppRoute