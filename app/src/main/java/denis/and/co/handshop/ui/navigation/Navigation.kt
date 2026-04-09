package denis.and.co.handshop.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object RecommendationRoute

@Serializable
object SearchByCategoryRoute

@Serializable
data class ProductDetailsRoute(val productId: String)

@Serializable
object CreateProductRoute

@Serializable
data class ProfileRoute(val sellerId: String? = null)

@Serializable
object LikedRoute

@Serializable
data class EditProductRoute(val productId: String)

@Serializable
object LoginRoute

@Serializable
object CreateProfileRoute

@Serializable
object EditProfileRoute