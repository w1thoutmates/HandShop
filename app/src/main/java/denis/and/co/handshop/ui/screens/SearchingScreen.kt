package denis.and.co.handshop.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import denis.and.co.handshop.R
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.data.model.CategoryProvider
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.components.ProductListItem
import denis.and.co.handshop.ui.navigation.ProductDetailsRoute
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.GreyText
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.viewmodel.CatalogViewModel
import denis.and.co.handshop.viewmodel.LikedViewModel
import denis.and.co.handshop.viewmodel.MetricsViewModel

@Composable
fun SearchingScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel,
    likedViewModel: LikedViewModel,
    metricsViewModel: MetricsViewModel
) {
    var input by remember { mutableStateOf("") }
    val uiState by catalogViewModel.state.collectAsState()
    val isSearching by catalogViewModel.isSearching.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBack)
//            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
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
                        onValueChange = { newValue -> input = newValue },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 2.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = BlackText,
                            unfocusedTextColor = GreyText,
                            focusedContainerColor = WhiteText,
                            unfocusedContainerColor = WhiteText,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedLabelColor = GreyText,
                            unfocusedLabelColor = GreyText
                        ),
                        placeholder = {
                            Text(
                                "Найти в Ручной Лавке",
                                style = TextStyle(
                                    fontFamily = Comfortaa,
                                    color = LowAlphaBlackText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                    )

                    Button(
                        onClick = {
                            if (input.isNotBlank()) {
                                catalogViewModel.search(input)
                            }
                        },
                        shape = RoundedCornerShape(0.dp, 14.dp, 14.dp, 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = WhiteText),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .padding()
                            .width(50.dp),
                    ) {
                        Image(
                            painterResource(R.drawable.search_icon),
                            contentDescription = "Иконка поиска",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

            if (isSearching) {
                Row(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .clickable {
                        catalogViewModel.resetSearch()
                    },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = LowAlphaBlackText
                    )

                    Text(
                        text = "назад к категориям",
                        style = TextStyle(
                            fontFamily = Comfortaa,
                            color = LowAlphaBlackText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                    )
                }
                Text(
                    text = "Результаты запроса",
                    style = TextStyle(
                        fontFamily = Comfortaa,
                        color = BlackText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    ),
                    maxLines = 1,
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                )
            }

            if (!isSearching)
                SearchingScreenContent(PaddingValues(0.dp), catalogViewModel)
            else
                SearchResultsContent(
                    state = uiState,
                    onProductClick = { id ->
                        navController.navigate(ProductDetailsRoute(id))
                    },
                    catalogViewModel = catalogViewModel,
                    likedViewModel = likedViewModel,
                    metricsViewModel = metricsViewModel
                )
        }

        AppFooter(navController)
    }
}

@Composable
fun SearchingScreenContent(
    modifier: PaddingValues,
    viewModel: CatalogViewModel
) {
    LazyColumn(modifier = Modifier.padding(modifier)) {
        items(
            CategoryProvider.categories
        ) { category ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.loadProductsByCategory(category.name)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter =  painterResource(category.iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                        .padding(start = 10.dp),
                    colorFilter = ColorFilter.tint(BlackText.copy(0.65f))
                )

                Text(
                    text = category.name,
                    style = TextStyle(
                        fontFamily = Comfortaa,
                        color = BlackText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .weight(1f)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(end = 15.dp)
                )
            }
        }
    }
}

@Composable
fun SearchResultsContent(
    state: CatalogState,
    onProductClick: (String) -> Unit,
    catalogViewModel: CatalogViewModel,
    likedViewModel: LikedViewModel,
    metricsViewModel: MetricsViewModel
) {
    when (state) {
        is CatalogState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(color = Accent)
            }
        }

        is CatalogState.Success -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.items) { item ->
                    ProductListItem(
                        product = item.product,
                        seller = item.seller,
                        onClick = {
                            onProductClick(item.product.id)
                            catalogViewModel.updateProductViewsCount(item.product.id)
                            metricsViewModel.updateProductClickStat(item.product)
                        },
                        viewModel = likedViewModel,
                        catalogViewModel = catalogViewModel
                    )
                }
            }
        }

        is CatalogState.Empty -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Ничего не найдено",
                    modifier = Modifier.align(Alignment.Center),
                    fontFamily = Onest,
                    color = LowAlphaBlackText,
                    fontSize = 20.sp
                )
            }
        }

        is CatalogState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message)
            }
        }
    }
}
