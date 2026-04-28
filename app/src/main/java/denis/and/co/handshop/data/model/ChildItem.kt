package denis.and.co.handshop.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import denis.and.co.handshop.ui.navigation.AppRoute

data class ChildItem(
    val id: String,
    val title: String,
    val description: String = "",
    val route: AppRoute,
    val icon: ImageVector
)