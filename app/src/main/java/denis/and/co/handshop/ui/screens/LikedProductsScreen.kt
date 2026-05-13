package denis.and.co.handshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import denis.and.co.handshop.R
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.components.LikedProductListItem
import denis.and.co.handshop.ui.navigation.ProductDetailsRoute
import denis.and.co.handshop.ui.navigation.ProfileRoute
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.GreyText
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.viewmodel.LikedViewModel
import denis.and.co.handshop.viewmodel.MetricsViewModel

@Composable
fun LikedProductsScreen(
    navController: NavController,
    viewModel: LikedViewModel,
    metricsViewModel: MetricsViewModel
) {
    val uiState by viewModel.state.collectAsState()
    val productCount = if (uiState is CatalogState.Success) {
        (uiState as CatalogState.Success).items.size
    } else 0

    LaunchedEffect(Unit) {
        viewModel.loadLikedProducts()
    }

    Scaffold(
        bottomBar = { AppFooter(navController) },
        containerColor = SoftBack,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LikedScreenHeader(
                navController = navController,
                count = productCount,
                viewModel = viewModel
            )

            LikedScreenContent(
                uiState = uiState,
                modifier = Modifier.weight(1f),
                onProductClick = { id -> navController.navigate(ProductDetailsRoute(id)) },
                onSellerClick = { id ->
                    navController.navigate(ProfileRoute(sellerId = id))
                    metricsViewModel.updateProfileClicks(id)
                },
                onDeleteProduct = { id, sellerId -> viewModel.deleteFromLiked(id, sellerId) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun LikedScreenContent(
    uiState: CatalogState,
    modifier: Modifier = Modifier,
    onProductClick: (String) -> Unit,
    onSellerClick: (String) -> Unit,
    onDeleteProduct: (String, String) -> Unit,
    viewModel: LikedViewModel
) {
    Box(modifier = modifier.fillMaxSize().background(SoftBack)) {
        when (val state = uiState) {
            is CatalogState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Accent)
            }
            is CatalogState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.product.id }) { item ->
                        val isInactive = item.product.status != ProductStatus.ACTIVE
                        val context = LocalContext.current
                        Box(modifier = Modifier.alpha(if (isInactive) 0.5f else 1f)) {
                            LikedProductListItem(
                                item = item,
                                onProductClick = onProductClick,
                                onSellerClick = onSellerClick,
                                onDeleteClick = {
                                    onDeleteProduct(item.product.id, item.product.sellerId)
                                    Toast.makeText(context, "Объявление [${item.product.title}] успешно удалено из избранного", Toast.LENGTH_LONG).show()
                                },
                                onShareClick = { /* sharing logic */ }
                            )
                            if (isInactive) {
                                Text(
                                    text = item.product.status.value,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 15.dp, end = 20.dp),
                                    style = TextStyle(
                                        fontFamily = Onest,
                                        color = BlackText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
            is CatalogState.Empty -> {
                // картинку разбитого сердца или пустой корзины над текстом поместить
                Text(
                    text = "В избранном пока пусто",
                    modifier = Modifier.align(Alignment.Center),
                    fontFamily = Onest,
                    color = LowAlphaBlackText,
                    fontSize = 18.sp
                )
            }
            is CatalogState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = Color.Red)
                    Button(onClick = { viewModel.loadLikedProducts() }) {
                        Text("Повторить")
                    }
                }
            }
        }
    }
}

@Composable
fun LikedScreenHeader(
    navController: NavController,
    count: Int,
    viewModel: LikedViewModel
) {
    var sortOption by remember { mutableStateOf("Сначала новые") }
    var showSortMenu by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HardBack)
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .background(SoftBack)
                .clip(RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                .background(HardBack)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Кнопка назад",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { navController.popBackStack() },
                        tint = BlackText,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.4f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Избранное  ",
                            style = TextStyle(
                                fontFamily = Onest,
                                color = BlackText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            ),
                            maxLines = 1
                        )

                        Text(
                            text = when {
                                count % 10 == 1 && count % 100 != 11 -> "$count товар"
                                count % 10 in 2..4 && count % 100 !in 12..14 -> "$count товара"
                                else -> "$count товаров"
                            },
                            style = TextStyle(
                                fontFamily = Comfortaa,
                                color = LowAlphaBlackText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                            ),
                            maxLines = 1
                        )
                    }
                }

                Box(
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Image(
                        painter = when (sortOption) {
                            "Дороже" -> {
                                painterResource(R.drawable.sort_by_desc)
                            }

                            "Дешевле" -> {
                                painterResource(R.drawable.sort_by_asc)
                            }

                            "Скрытые" -> {
                                painterResource(R.drawable.eye)
                            }

                            "Проданные" -> {
                                painterResource(R.drawable.cart_nav)
                            }

                            "Только активные" -> {
                                painterResource(R.drawable.mark)
                            }

                            else -> {
                                painterResource(R.drawable.sort_by_news)
                            }
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .size(30.dp)
                            .clickable {
                                showSortMenu = true
                            }
                    )

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(15.dp))
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White, RoundedCornerShape(15.dp)),
                        containerColor = Color.White,
                        shape = RoundedCornerShape(15.dp),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        offset = DpOffset(x = (-16).dp, y = 0.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.sort_by_news),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Сначала новые",
                                        style = TextStyle(
                                            fontFamily = Comfortaa,
                                            fontSize = 14.sp,
                                            color = if (sortOption == "Сначала новые") BlackText else LowAlphaBlackText
                                        ),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            },
                            onClick = {
                                sortOption = "Сначала новые"
                                showSortMenu = false
                                viewModel.sortLikedProducts("Сначала новые")
                            },

                            )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.sort_by_desc),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Дороже",
                                        style = TextStyle(
                                            fontFamily = Comfortaa,
                                            fontSize = 14.sp,
                                            color = if (sortOption == "Дороже") BlackText else LowAlphaBlackText
                                        ),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            },
                            onClick = {
                                sortOption = "Дороже"
                                showSortMenu = false
                                viewModel.sortLikedProducts("Дороже")
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.sort_by_asc),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Дешевле",
                                        style = TextStyle(
                                            fontFamily = Comfortaa,
                                            fontSize = 14.sp,
                                            color = if (sortOption == "Дешевле") BlackText else LowAlphaBlackText
                                        ),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            },
                            onClick = {
                                sortOption = "Дешевле"
                                showSortMenu = false
                                viewModel.sortLikedProducts("Дешевле")
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Скрытые",
                                        style = TextStyle(
                                            fontFamily = Comfortaa,
                                            fontSize = 14.sp,
                                            color = if (sortOption == "Скрытые") BlackText else LowAlphaBlackText
                                        ),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            },
                            onClick = {
                                sortOption = "Скрытые"
                                showSortMenu = false
                                viewModel.sortLikedProducts("Скрытые")
                            },
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.cart_nav),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Проданные",
                                        style = TextStyle(
                                            fontFamily = Comfortaa,
                                            fontSize = 14.sp,
                                            color = if (sortOption == "Проданные") BlackText else LowAlphaBlackText
                                        ),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            },
                            onClick = {
                                sortOption = "Проданные"
                                showSortMenu = false
                                viewModel.sortLikedProducts("Проданные")
                            },
                        )

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Только активные",
                                        style = TextStyle(
                                            fontFamily = Comfortaa,
                                            fontSize = 14.sp,
                                            color = if (sortOption == "Только активные") BlackText else LowAlphaBlackText
                                        ),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            },
                            onClick = {
                                sortOption = "Только активные"
                                showSortMenu = false
                                viewModel.sortLikedProducts("Только активные")
                            },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SearchField(
                    input = input,
                    onValueChange = {
                        input = it
                        viewModel.searchInLiked(it)
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun SearchField(
    input: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LikedViewModel
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(45.dp)
            .dropShadow(
                shape = RoundedCornerShape(15.dp),
                shadow = Shadow(
                    radius = 5.dp,
                    offset = DpOffset(x = 0.dp, y = 3.dp),
                    alpha = 0.35f
                )
            )
    ) {
        TextField(
            value = input,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = BlackText,
                unfocusedTextColor = GreyText,
                focusedContainerColor = WhiteText,
                unfocusedContainerColor = WhiteText,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            placeholder = {
                Text(
                    "Найти в избранном",
                    style = TextStyle(
                        fontFamily = Comfortaa,
                        color = LowAlphaBlackText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    ),
                )
            },
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(50.dp)
                        .background(Accent, RoundedCornerShape(0.dp, 14.dp, 14.dp, 0.dp))
                        .clickable { viewModel.searchInLiked(input) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(R.drawable.search_icon),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        )
    }
}