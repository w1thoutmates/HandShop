package denis.and.co.handshop.ui.screens.metrics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.segmented
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.*
import denis.and.co.handshop.viewmodel.ReviewsViewModel
import java.util.Locale

@Composable
fun SellerRateMetricScreen(
    navController: NavController,
    viewModel: ReviewsViewModel
) {
    val points by viewModel.ratingPoints.collectAsState()
    val seller by viewModel.seller.collectAsState()

    val modelProducer = remember { CartesianChartModelProducer.build() }
    val marker = rememberMarker(true)

    val basePalette = listOf(
        Accent, Color(0xFF00B894), Color(0xFFFF7675),
        Color(0xFFFDCB6E), Color(0xFF0984E3), Color(0xFFE17055)
    )

    val columnComponents = basePalette.map { color ->
        rememberLineComponent(
            color = color,
            thickness = 32.dp,
            shape = Shape.rounded(allDp = 4f),
            dynamicShader = DynamicShader.verticalGradient(
                arrayOf(color, color.copy(alpha = 0.7f))
            )
        )
    }

    val multiColorProvider = remember(columnComponents) {
        object : ColumnCartesianLayer.ColumnProvider {
            override fun getColumn(
                entry: ColumnCartesianLayerModel.Entry,
                seriesIndex: Int,
                extraStore: ExtraStore
            ): LineComponent {
                val index = entry.x.toInt()
                return columnComponents[index % columnComponents.size]
            }

            override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore): LineComponent =
                columnComponents[0]
        }
    }

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            modelProducer.tryRunTransaction {
                columnSeries { series(points.map { it.y }) }
            }
        }
    }

    Scaffold(
        containerColor = SoftBack,
        bottomBar = { AppFooter(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            HeaderSection(navController, "Рейтинг продавца")

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Text(
                    text = "Текущий рейтинг: ${String.format("%.1f", seller?.rate ?: 0.0)}",
                    fontFamily = Onest,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = BlackText
                )
                Text(
                    text = "На основе ${points.size} отзывов",
                    fontFamily = Onest,
                    fontSize = 14.sp,
                    color = BlackText.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(15.dp))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                if (points.isNotEmpty()) {
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberColumnCartesianLayer(
                                columnProvider = multiColorProvider,
                                axisValueOverrider = remember {
                                    com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider.fixed(
                                        minY = 0f,
                                        maxY = 5f
                                    )
                                }
                            ),
                            startAxis = rememberStartAxis(
                                label = rememberAxisLabelComponent(
                                    color = BlackText,
                                    textSize = 12.sp
                                ),
                                guideline = rememberLineComponent(color = BlackText.copy(0.1f)),
                                itemPlacer = com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer.Vertical.count(
                                    count = { 6 }
                                )
                            ),
                            bottomAxis = rememberBottomAxis(
                                label = rememberAxisLabelComponent(
                                    color = BlackText.copy(alpha = 0.6f),
                                    textSize = 11.sp
                                ),
                                valueFormatter = { value, _, _ ->
                                    "${value.toInt() + 1}"
                                },
                                guideline = null
                            )
                        ),
                        modelProducer = modelProducer,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        marker = marker,
                        horizontalLayout = HorizontalLayout.segmented()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Нет данных для графика", fontFamily = Onest)
                    }
                }
            }
        }
    }
}