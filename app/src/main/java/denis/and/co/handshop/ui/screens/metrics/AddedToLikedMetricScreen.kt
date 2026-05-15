package denis.and.co.handshop.ui.screens.metrics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import denis.and.co.handshop.data.model.Product
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
fun AddedToLikedMetricScreen(
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
    val removedPoints by viewModel.removedFromLikedPoints.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val seller by profileViewModel.seller.collectAsState()

    val modelProducer = remember { CartesianChartModelProducer.build() }
    val removedModelProducer = remember { CartesianChartModelProducer.build() }
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM", Locale("ru")) }

    val globalMaxY = remember(points, removedPoints) {
        val maxAdded = points.maxOfOrNull { it.y } ?: 0f
        val maxRemoved = removedPoints.maxOfOrNull { it.y } ?: 0f
        maxOf(maxAdded, maxRemoved).coerceAtLeast(1f)
    }

    LaunchedEffect(Unit) {
        viewModel.loadProductsAndInitialStats(sellerId, days)
        profileViewModel.loadProfile(sellerId)
    }

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            modelProducer.tryRunTransaction {
                columnSeries { series(points.map { it.y }) }
            }
        }
    }

    LaunchedEffect(removedPoints) {
        if (removedPoints.isNotEmpty()) {
            removedModelProducer.tryRunTransaction {
                columnSeries { series(removedPoints.map { it.y }) }
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
                    .verticalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                HeaderSection(navController, "Добавлено и удалено из избранного", currentSeller)

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
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
                                onClick = { viewModel.selectProduct(product, days) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimePeriod.entries.forEach { period ->
                            FilterChip(
                                label = period.label,
                                isSelected = days == period.days,
                                onClick = {
                                    days = period.days
                                    selectedProduct?.let { viewModel.selectProduct(it, days) }
                                },
                                seller = currentSeller
                            )
                        }
                    }
                }

                Text(
                    text = "Добавлено в избранное",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    fontFamily = Onest,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(currentSeller.selfProfileTextColor.toColorInt())
                )

                Card(
                    modifier = Modifier.fillMaxWidth().height(350.dp).padding(16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    if (points.isNotEmpty()) {
                        CartesianChartHost(
                            chart = rememberCartesianChart(
                                rememberColumnCartesianLayer(
                                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                                        rememberLineComponent(
                                            color = accentColor,
                                            thickness = 12.dp,
                                            shape = Shape.rounded(allDp = 4f),
                                            dynamicShader = DynamicShader.verticalGradient(
                                                arrayOf(accentColor, accentColor.copy(alpha = 0.6f))
                                            )
                                        )
                                    ),
                                    axisValueOverrider = AxisValueOverrider.fixed(maxY = globalMaxY)
                                ),
                                startAxis = rememberStartAxis(
                                    label = rememberAxisLabelComponent(color = BlackText, textSize = 12.sp),
                                    guideline = rememberLineComponent(BlackText.copy(0.1f)),
                                    itemPlacer = AxisItemPlacer.Vertical.step(step = { 1f }, shiftTopLines = false)
                                ),
                                bottomAxis = rememberBottomAxis(
                                    valueFormatter = { value, _, _ ->
                                        stats.getOrNull(value.toInt())?.let {
                                            try { LocalDate.parse(it.date).format(formatter) } catch (ex: Exception) { "" }
                                        } ?: ""
                                    }
                                )
                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Нет данных за этот период", fontFamily = Onest)
                        }
                    }
                }

                Text(
                    text = "Удалено из избранного",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    fontFamily = Onest,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(currentSeller.selfProfileTextColor.toColorInt())
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    val removeColor = Color(0xFFD32F2F)

                    if (removedPoints.isNotEmpty()) {

                        CartesianChartHost(
                            chart = rememberCartesianChart(
                                rememberColumnCartesianLayer(
                                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                                        rememberLineComponent(
                                            color = removeColor,
                                            thickness = 12.dp,
                                            shape = Shape.rounded(allDp = 4f),
                                            dynamicShader = DynamicShader.verticalGradient(
                                                arrayOf(
                                                    removeColor,
                                                    removeColor.copy(alpha = 0.55f)
                                                )
                                            )
                                        )
                                    ),
                                    axisValueOverrider = AxisValueOverrider.fixed(maxY = globalMaxY)
                                ),

                                startAxis = rememberStartAxis(
                                    label = rememberAxisLabelComponent(
                                        color = BlackText,
                                        textSize = 12.sp
                                    ),
                                    guideline = rememberLineComponent(
                                        BlackText.copy(alpha = 0.1f)
                                    ),
                                    itemPlacer = AxisItemPlacer.Vertical.step(
                                        step = { 1f },
                                        shiftTopLines = false
                                    )
                                ),

                                bottomAxis = rememberBottomAxis(
                                    valueFormatter = { value, _, _ ->
                                        stats.getOrNull(value.toInt())?.let {
                                            try {
                                                LocalDate.parse(it.date).format(formatter)
                                            } catch (_: Exception) {
                                                ""
                                            }
                                        } ?: ""
                                    }
                                )
                            ),

                            modelProducer = removedModelProducer,

                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )

                    } else {

                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Нет данных за этот период",
                                fontFamily = Onest
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductSelectionCard(
    product: Product,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(2.dp, accentColor) else null,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrls.first(),
                contentDescription = null,
                modifier = Modifier.height(80.dp).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = product.title,
                modifier = Modifier.padding(8.dp),
                fontSize = 10.sp,
                maxLines = 1,
                fontFamily = Onest,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}