package denis.and.co.handshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.components.ProductListItem
import denis.and.co.handshop.ui.navigation.EditProductRoute
import denis.and.co.handshop.ui.navigation.ProductDetailsRoute
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.viewmodel.CatalogViewModel
import denis.and.co.handshop.viewmodel.LikedViewModel
import denis.and.co.handshop.viewmodel.ProfileViewModel

@Composable
fun ExpandedPublishedProductsScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel,
    likedViewModel: LikedViewModel,
    profileViewModel: ProfileViewModel,
    sellerId: String?
) {
    val publishedProducts = profileViewModel.sellerProducts.collectAsState()
    val isMyProfile = sellerId == null || sellerId == profileViewModel.currentUid
    val seller by profileViewModel.seller.collectAsState()

    LaunchedEffect(sellerId) {
        profileViewModel.loadProfile(sellerId)
    }

    if (seller == null) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Accent
            )
        }
        return
    }

    seller?.let { currentSeller ->
        Scaffold(
            contentColor = Color(currentSeller.selfProfileBackground.toColorInt()),
            bottomBar = { AppFooter(navController, currentSeller) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(currentSeller.selfProfileBackground.toColorInt()))
                    .padding(padding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 15.dp)
                        .background(Color(currentSeller.selfProfileBackground.toColorInt()))
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .size(40.dp)
                            .clickable { navController.popBackStack() },
                        tint = Color(currentSeller.selfProfileIconsColor.toColorInt())
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(start = 10.dp, top = 5.dp)
                            .background(Color(currentSeller.selfProfileBackground.toColorInt()))
                    ) {
                        Text(
                            text = "Опубликованные товары",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = Onest,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(currentSeller.selfProfileTextColor.toColorInt())
                            )
                        )

                        val count = publishedProducts.value.count()

                        Text(
                            text = "$count ${if (count == 1) "активный" else "активных"}",
                            fontFamily = Comfortaa,
                            fontSize = 14.sp,
                            color = Color(currentSeller.selfProfileTextColor.toColorInt()).copy(alpha = 0.5f)
                        )
                    }
                }

                Content(
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    viewModel = catalogViewModel,
                    likedViewModel = likedViewModel,
                    seller = currentSeller,
                    isMyProfile = isMyProfile
                )
            }
        }
    }
}

@Composable
fun Content(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CatalogViewModel = viewModel(),
    likedViewModel: LikedViewModel,
    seller: Seller,
    isMyProfile: Boolean
) {
    val uiState by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is CatalogState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(seller.selfProfileAccentColor.toColorInt())
                )
            }
            is CatalogState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(seller.selfProfileBackground.toColorInt()))
                ) {
                    items(state.items) { item ->
                        ProductListItem(
                            product = item.product,
                            onClick = {
                                if (isMyProfile) {
                                    navController.navigate(EditProductRoute(item.product.id))
                                } else {
                                    navController.navigate(ProductDetailsRoute(item.product.id))
                                }
                            },
                            seller = seller,
                            viewModel = likedViewModel,
                            isMyProfile = isMyProfile
                        )
                    }
                }
            }
            is CatalogState.Empty -> {
                Text(
                    text = "Ничего не найдено",
                    modifier = Modifier.align(Alignment.Center),
                    fontFamily = Onest,
                    color = Color(seller.selfProfileTextColor.toColorInt()).copy(0.66f),
                    fontSize = 20.sp
                )
            }
            is CatalogState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = Color.Red)
                    Button(onClick = { viewModel.loadRecommendations() }) {
                        Text("Повторить")
                    }
                }
            }
        }
    }
}