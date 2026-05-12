package denis.and.co.handshop.ui.screens.metrics

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.DefaultPointConnector
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import denis.and.co.handshop.data.enums.TimePeriod
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.viewmodel.MetricsViewModel
import denis.and.co.handshop.viewmodel.ProfileViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitorsCostCompareMetricScreen(
    navController: NavController,
    viewModel: MetricsViewModel,
    profileViewModel: ProfileViewModel,
    sellerId: String
) {
    var days by remember { mutableIntStateOf(365) }
    var searchQuery by remember { mutableStateOf("") }

    val products by viewModel.products.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val comparisonData by viewModel.priceComparisonData.collectAsState()
    val seller by profileViewModel.seller.collectAsState()

    val modelProducer = remember { CartesianChartModelProducer.build() }
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM", Locale("ru")) }

    val sellerAccent = remember(seller) {
        try {
            Color(seller?.selfProfileAccentColor?.toColorInt() ?: 0xFF6200EE.toInt())
        } catch (e: Exception) {
            Color(0xFF6200EE)
        }
    }

    val isOverpriced = remember(comparisonData) {
        val lastUserPrice = comparisonData?.userPrices?.lastOrNull() ?: 0f
        val lastMarketPrice = comparisonData?.marketMedianPrices?.lastOrNull() ?: 0f
        if (lastMarketPrice == 0f) false else lastUserPrice > lastMarketPrice * 1.15f
    }

    val dynamicLineColor by animateColorAsState(
        targetValue = if (isOverpriced) Color(0xFFE53935) else sellerAccent,
        animationSpec = tween(durationMillis = 500),
        label = "LineColorAnimation"
    )

    LaunchedEffect(Unit) {
        viewModel.loadProductsAndSelectLastPublished(sellerId, days)
        profileViewModel.loadProfile(sellerId)
    }

    LaunchedEffect(comparisonData) {
        comparisonData?.let { data ->
            modelProducer.tryRunTransaction {
                lineSeries {
                    series(data.userPrices)
                    series(data.marketMedianPrices)
                }
            }
        }
    }

    seller?.let { currentSeller ->
        Scaffold(
            containerColor = Color(currentSeller.selfProfileBackground.toColorInt()),
            bottomBar = { AppFooter(navController, currentSeller) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                HeaderSection(navController, "Индекс цен", currentSeller)

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        placeholder = { Text("Поиск товара", fontFamily = Onest) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = sellerAccent
                        )
                    )

                    val filteredProducts = products.filter { it.title.contains(searchQuery, ignoreCase = true) }

                    LazyRow(
                        contentPadding = PaddingValues(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductSelectionCard(
                                product = product,
                                isSelected = selectedProduct?.id == product.id,
                                accentColor = sellerAccent,
                                onClick = { viewModel.loadPriceIndexStats(product, days) }
                            )
                        }
                    }

//                    Row(
//                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        TimePeriod.entries.forEach { period ->
//                            FilterChip(
//                                label = period.label,
//                                isSelected = days == period.days,
//                                onClick = {
//                                    days = period.days
//                                    selectedProduct?.let {
//                                        viewModel.selectProduct(it, days)
//                                        viewModel.loadPriceIndexStats(it, days)
//                                    }
//                                },
//                                seller = currentSeller
//                            )
//                        }
//                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().height(400.dp).padding(16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LegendItem("Ваша цена", dynamicLineColor)
                            LegendItem("Медиана рынка", Color(0xFF545454).copy(alpha = 0.6f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (comparisonData != null) {
                            CartesianChartHost(
                                chart = rememberCartesianChart(
                                    rememberLineCartesianLayer(
                                        lines = listOf(
                                            rememberLineSpec(
                                                shader = DynamicShader.color(dynamicLineColor),
                                                thickness = 4.dp,
                                                pointConnector = DefaultPointConnector(0.2f)
                                            ),
                                            rememberLineSpec(
                                                shader = DynamicShader.color(Color(0xFF545454).copy(alpha = 0.6f)),
                                                thickness = 2.dp,
                                                pointConnector = DefaultPointConnector(0.2f)
                                            )
                                        )
                                    ),
                                    startAxis = rememberStartAxis(
                                        valueFormatter = { value, _, _ -> "${value.toInt()} ₽" },
                                        itemPlacer = com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer.Vertical.step(step = { 5f })
                                    ),
                                    bottomAxis = rememberBottomAxis(
                                        valueFormatter = { value, _, _ ->
                                            comparisonData?.dates?.getOrNull(value.toInt())?.let {
                                                try { LocalDate.parse(it).format(formatter) } catch (ex: Exception) { "" }
                                            } ?: ""
                                        }
                                    )
                                ),
                                modelProducer = modelProducer,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Выберите товар для анализа", fontFamily = Onest, color = Color.Gray)
                            }
                        }
                    }
                }

                selectedProduct?.let {
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            text = if (isOverpriced)
                                "Ваша цена значительно выше рыночной. Рекомендуем снизить её для увеличения охватов."
                            else "Ваша цена соответствует рыночной или ниже её. Отличная работа!",
                            fontFamily = Onest,
                            fontSize = 13.sp,
                            color = if (isOverpriced) Color(0xFFE53935) else BlackText.copy(0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, fontSize = 12.sp, fontFamily = Onest, fontWeight = FontWeight.Medium)
    }
}