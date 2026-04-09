package denis.and.co.handshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import denis.and.co.handshop.R
import denis.and.co.handshop.data.model.CatalogState
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.components.ProductListItem
import denis.and.co.handshop.ui.navigation.CreateProductRoute
import denis.and.co.handshop.ui.navigation.LikedRoute
import denis.and.co.handshop.ui.navigation.ProductDetailsRoute
import denis.and.co.handshop.ui.navigation.ProfileRoute
import denis.and.co.handshop.ui.navigation.RecommendationRoute
import denis.and.co.handshop.ui.navigation.SearchByCategoryRoute
import denis.and.co.handshop.ui.theme.*
import denis.and.co.handshop.viewmodel.CatalogViewModel

@Composable
fun RecommendationScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel
    ) {
    val listState = rememberLazyListState()

    LaunchedEffect(catalogViewModel.scrollTrigger) {
        if (catalogViewModel.scrollTrigger > 0) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = { Header(onSearch = { query ->
            catalogViewModel.search(query)
        }) },
        bottomBar = { AppFooter(navController) },
        modifier = Modifier
            .background(SoftBack)
            .fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                text = "Рекомендуем сегодня",
                style = TextStyle(
                    fontFamily = Comfortaa,
                    color = BlackText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                ),
                maxLines = 1,
                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
            )

            Content(
                modifier = Modifier.weight(1f),
                viewModel = catalogViewModel,
                onProductClick = { id ->
                    navController.navigate(ProductDetailsRoute(id))
                }
            )
        }
    }
}

@Composable
fun Header(onSearch: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))
            .padding(0.dp)
            .background(HardBack)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(top = 5.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.app_icon),
                contentDescription = "Логотип приложения",
                Modifier.padding(start = 25.dp, end = 30.dp, bottom = 5.dp).size(100.dp, 50.dp),
                contentScale = ContentScale.FillBounds
            )

            Button(
                onClick = { /* catalog open fun + catalog icon anim. */ },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = WhiteText),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.catalog_icon),
                    contentDescription = "Каталог",
                    Modifier.padding(start = 0.dp, end = 7.dp).size(20.dp),
                    contentScale = ContentScale.FillBounds
                )

                Text(text = "Каталог", style = Typography.labelSmall)

            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 40.dp)
        ) {
            var input by remember { mutableStateOf("") }
            Box(
                modifier = Modifier
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
                    onValueChange = { newValue -> input = newValue },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 2.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = BlackText,
                        unfocusedTextColor = GreyText,
                        focusedContainerColor = WhiteText,
                        unfocusedContainerColor = WhiteText,
                        focusedIndicatorColor =  Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = GreyText,
                        unfocusedLabelColor = GreyText
                    ),
                    placeholder = {
                        Text(
                            "Найти в Ручной Лавке",
                            style = TextStyle(
                                fontFamily = Comfortaa,
                                color = Color(0x66000000),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                Button(
                    onClick = { onSearch(input) },
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
                        .width(50.dp)
                    ,
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
    }
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    viewModel: CatalogViewModel = viewModel(),
    onProductClick: (String) -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is CatalogState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Accent
                )
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
                            onClick = {
                                onProductClick(item.product.id)
                                viewModel.updateProductViewsCount(item.product.id)
                            },
                            seller = item.seller
                        )
                    }
                }
            }
            is CatalogState.Empty -> {
                Text(
                    text = "Ничего не найдено",
                    modifier = Modifier.align(Alignment.Center),
                    fontFamily = Onest,
                    color = LowAlphaBlackText,
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

@Composable
public fun Footer(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
            .background(HardBack),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.padding(top = 17.dp, bottom = 17.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.home_nav),
                contentDescription = "Домой навигация",
                modifier = Modifier
                    .size(30.dp, 30.dp)
                    .clickable {
                        val isAlreadyOnRecommendation = currentDestination?.hasRoute<RecommendationRoute>() == true

                        if (!isAlreadyOnRecommendation) {
                            navController.navigate(RecommendationRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                contentScale = ContentScale.FillBounds
            )

            Spacer(Modifier.width(45.dp))

            Image(
                painter = painterResource(R.drawable.search_nav),
                contentDescription = "Поиск навигация",
                modifier = Modifier
                    .size(30.dp, 30.dp)
                    .clickable { navController.navigate(SearchByCategoryRoute) },
                contentScale = ContentScale.FillBounds,
                alpha = 0.5f
            )

            Spacer(Modifier.width(45.dp))

            Image(
                painter = painterResource(R.drawable.add_image),
                contentDescription = "Создать объявление",
                modifier = Modifier
                    .size(30.dp, 30.dp)
                    .clickable { navController.navigate(CreateProductRoute) },
                contentScale = ContentScale.FillBounds,
                alpha = 0.5f
            )

            Spacer(Modifier.width(45.dp))

            Image(
                painter = painterResource(R.drawable.liked_nav),
                contentDescription = "Избранное навигация",
                modifier = Modifier
                    .size(30.dp, 30.dp)
                    .clickable { navController.navigate(LikedRoute) },
                contentScale = ContentScale.FillBounds,
                alpha = 0.5f
            )

            Spacer(Modifier.width(45.dp))

            Image(
                painter = painterResource(R.drawable.profile_nav),
                contentDescription = "Профиль навигация",
                modifier = Modifier
                    .size(30.dp, 30.dp)
                    .clickable { navController.navigate(ProfileRoute()) },
                contentScale = ContentScale.FillBounds,
                alpha = 0.5f
            )

        }
    }
}