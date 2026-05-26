package denis.and.co.handshop.ui.screens.metrics

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
import androidx.compose.ui.text.style.TextAlign
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
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
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
fun CTRMetricScreen(
    navController: NavController,
    viewModel: MetricsViewModel,
    profileViewModel: ProfileViewModel,
    sellerId: String
) {
    var days by remember { mutableIntStateOf(7) }
    var searchQuery by remember { mutableStateOf("") }

    val products by viewModel.products.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val points by viewModel.reachPoints.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val seller by profileViewModel.seller.collectAsState()

    val modelProducer = remember { CartesianChartModelProducer.build() }
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM", Locale("ru")) }

    LaunchedEffect(Unit) {
        viewModel.loadProductsAndInitialStats(sellerId, days)
        profileViewModel.loadProfile(sellerId)
    }

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            modelProducer.tryRunTransaction {
                lineSeries { series(points.map { it.y }) }
            }
        }
    }

    seller?.let { currentSeller ->
        val accentColor = Color(currentSeller.selfProfileAccentColor.toColorInt())

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
                HeaderSection(navController, "Эффективность (CTR)", currentSeller)

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        placeholder = { Text("Поиск товара", fontFamily = Onest) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = accentColor
                        )
                    )

                    val filteredProducts = products.filter { it.title.contains(searchQuery, ignoreCase = true) }
                    LazyRow(
                        contentPadding = PaddingValues(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            ProductSelectionCard(
                                product = product,
                                isSelected = selectedProduct?.id == product.id,
                                accentColor = accentColor,
                                onClick = { viewModel.selectProductForCTR(product, days) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimePeriod.entries.forEach { period ->
                            FilterChip(
                                label = period.label,
                                isSelected = days == period.days,
                                onClick = {
                                    days = period.days
                                    selectedProduct?.let { viewModel.selectProductForCTR(it, days) }
                                },
                                seller = currentSeller
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Кликабельность в %",
                            fontFamily = Onest,
                            fontSize = 14.sp,
                            color = BlackText.copy(alpha = 0.5f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (points.isNotEmpty()) {
                            CartesianChartHost(
                                chart = rememberCartesianChart(
                                    rememberLineCartesianLayer(
                                        lines = listOf(
                                            rememberLineSpec(
                                                shader = DynamicShader.color(accentColor),
                                                thickness = 4.dp,
                                                pointConnector = DefaultPointConnector(0.25f),
                                                backgroundShader = DynamicShader.verticalGradient(
                                                    arrayOf(accentColor.copy(alpha = 0.4f), Color.Transparent)
                                                )
                                            )
                                        )
                                    ),
                                    startAxis = rememberStartAxis(
                                        label = rememberAxisLabelComponent(
                                            color = BlackText,
                                            textSize = 10.sp
                                        ),
                                        guideline = rememberLineComponent(BlackText.copy(0.05f)),
                                        valueFormatter = { value, _, _ ->
                                            "${String.format(Locale.ENGLISH, "%.1f", value)}%"
                                        }
                                    ),
                                    bottomAxis = rememberBottomAxis(
                                        label = rememberAxisLabelComponent(
                                            color = BlackText,
                                            textSize = 10.sp,
                                        ),
                                        valueFormatter = { value, _, _ ->
                                            stats.getOrNull(value.toInt())?.let {
                                                try {
                                                    LocalDate.parse(it.date).format(formatter)
                                                } catch (ex: Exception) { "" }
                                            } ?: ""
                                        }
                                    )
                                ),
                                modelProducer = modelProducer,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Нет данных по кликам\nза этот период",
                                    fontFamily = Onest,
                                    textAlign = TextAlign.Center,
                                    color = BlackText.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "CTR (Click-Through Rate) — это отношение кликов на карточку товара к его показам в ленте.",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    fontFamily = Onest,
                    color = BlackText.copy(alpha = 0.5f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}