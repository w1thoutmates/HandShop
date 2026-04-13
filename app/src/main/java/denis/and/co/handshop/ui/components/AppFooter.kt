package denis.and.co.handshop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import denis.and.co.handshop.R
import denis.and.co.handshop.di.AppDependencies.globalCatalogViewModel
import denis.and.co.handshop.ui.navigation.CreateProductRoute
import denis.and.co.handshop.ui.navigation.LikedRoute
import denis.and.co.handshop.ui.navigation.ProfileRoute
import denis.and.co.handshop.ui.navigation.RecommendationRoute
import denis.and.co.handshop.ui.navigation.SearchByCategoryRoute
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.SoftBack

@Composable
fun AppFooter(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SoftBack)
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
            .background(HardBack)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            FooterItem(
                iconRes = R.drawable.home_nav,
                isSelected = currentDestination?.hasRoute<RecommendationRoute>() == true,
                onClick = {
                    if (currentDestination?.hasRoute<RecommendationRoute>() == true) {
                        globalCatalogViewModel?.refreshAndScroll()
                    } else {
                        navController.navigate(RecommendationRoute) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                }
            )

            Spacer(Modifier.width(45.dp))

            FooterItem(
                iconRes = R.drawable.search_nav,
                isSelected = currentDestination?.hasRoute<SearchByCategoryRoute>() == true,
                onClick = { navController.navigate(SearchByCategoryRoute) }
            )

            Spacer(Modifier.width(45.dp))

            FooterItem(
                iconRes = R.drawable.add_image,
                isSelected = currentDestination?.hasRoute<CreateProductRoute>() == true,
                onClick = { navController.navigate(CreateProductRoute) }
            )

            Spacer(Modifier.width(45.dp))

            FooterItem(
                iconRes = R.drawable.liked_nav,
                isSelected = currentDestination?.hasRoute<LikedRoute>() == true,
                onClick = { navController.navigate(LikedRoute) }
            )

            Spacer(Modifier.width(45.dp))

            FooterItem(
                iconRes = R.drawable.profile_nav,
                isSelected = currentDestination?.hasRoute<ProfileRoute>() == true,
                onClick = { navController.navigate(ProfileRoute()) }
            )
        }
    }
}

@Composable
fun FooterItem(
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = Modifier
            .size(30.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .alpha(if (isSelected) 1f else 0.5f),
        contentScale = ContentScale.FillBounds
    )
}