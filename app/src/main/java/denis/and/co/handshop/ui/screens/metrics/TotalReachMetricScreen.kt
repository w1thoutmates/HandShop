package denis.and.co.handshop.ui.screens.metrics

import android.text.Layout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import denis.and.co.handshop.data.enums.TimePeriod
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.viewmodel.MetricsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.utils.getContrastColor
import denis.and.co.handshop.viewmodel.ProfileViewModel

@Composable
fun TotalReachMetricScreen(
    navController: NavController,
    viewModel: MetricsViewModel,
    profileViewModel: ProfileViewModel
) {
    var days by remember { mutableIntStateOf(7) }
    val points by viewModel.reachPoints.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM", Locale("ru")) }

    val modelProducer = remember { CartesianChartModelProducer.build() }

    val marker = rememberMarker()

    val seller by profileViewModel.seller.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(null)
    }

    val basePalette = listOf(
        Color(0xFF6C5CE7), Color(0xFF00B894), Color(0xFFFF7675),
        Color(0xFFFDCB6E), Color(0xFF0984E3), Color(0xFFE17055)
    )
    val columnComponents = basePalette.map { color ->
        rememberLineComponent(
            color = color,
            thickness = 16.dp,
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

            override fun getWidestSeriesColumn(
                seriesIndex: Int,
                extraStore: ExtraStore
            ): LineComponent {
                return columnComponents[0]
            }
        }
    }

    LaunchedEffect(days) {
        viewModel.loadStats(days)
    }

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            modelProducer.tryRunTransaction {
                columnSeries { series(points.map { it.y }) }

                lineSeries {
                    series(points.map { it.y })
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
            ) {
                HeaderSection(navController, "Охваты", currentSeller)

                val totalImpressions = points.sumOf { it.y.toInt() }
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Text(
                        text = "Всего показов: $totalImpressions",
                        fontFamily = Onest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt())
                    )
                    Text(
                        text = "Статистика по дням обновляется в реальном времени",
                        fontFamily = Onest,
                        fontSize = 14.sp,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt()).copy(alpha = 0.5f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimePeriod.entries.forEach { period ->
                            FilterChip(
                                label = period.label,
                                isSelected = days == period.days,
                                onClick = { days = period.days },
                                seller = currentSeller
                            )
                        }
                    }
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
                                    columnProvider = multiColorProvider
                                ),
                                rememberLineCartesianLayer(
                                    lines = listOf(
                                        rememberLineSpec(
                                            shader = DynamicShader.color(Accent.copy(alpha = 0.15f)),
                                            thickness = 2.dp,
                                            point = rememberShapeComponent(
                                                shape = Shape.Pill,
                                                color = Accent.copy(0.45f)
                                            ),
                                            pointSize = 5.dp,
                                        )
                                    )
                                ),
                                startAxis = rememberStartAxis(
                                    label = rememberAxisLabelComponent(
                                        color = BlackText,
                                        textSize = 12.sp
                                    ),
                                    guideline = rememberLineComponent(BlackText.copy(0.1f))
                                ),
                                bottomAxis = rememberBottomAxis(
                                    label = rememberAxisLabelComponent(
                                        color = BlackText.copy(alpha = 0.6f),
                                        textSize = 11.sp
                                    ),
                                    valueFormatter = { value, _, _ ->
                                        stats.getOrNull(value.toInt())?.let {
                                            try {
                                                LocalDate.parse(it.date).format(formatter)
                                            } catch (ex: Exception) {
                                                ""
                                            }
                                        } ?: ""
                                    },
                                    guideline = null
                                )
                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            marker = marker
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Нет данных для графика", fontFamily = Onest, color = Color(currentSeller.selfProfileTextColor.toColorInt()))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(navController: NavController, title: String, seller: Seller) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 15.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 15.dp)
                .size(40.dp)
                .clickable { navController.popBackStack() },
            tint = Color(seller.selfProfileIconsColor.toColorInt())
        )
        Text(
            text = title,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = Onest,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(seller.selfProfileTextColor.toColorInt())
            ),
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit, seller: Seller) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isSelected) Color(seller.selfProfileAccentColor.toColorInt()) else Color(seller.selfProfileFooterColor.toColorInt()).copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = Onest,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color(seller.selfProfileAccentTextColor.toColorInt()) else getContrastColor(Color(seller.selfProfileAccentTextColor.toColorInt()))
            )
        )
    }
}

@Composable
fun rememberMarker(flag: Boolean = false): CartesianMarker {
    val label = rememberTextComponent(
        color = Color.White,
        background = rememberShapeComponent(
            shape = Shape.rounded(5.dp),
            color = Accent
        ),
        padding = Dimensions.of(8.dp, 4.dp),
        textSize = 14.sp,
        textAlignment = Layout.Alignment.ALIGN_CENTER
    )

    val indicator = rememberShapeComponent(
        shape = Shape.rounded(5.dp),
        color = Color.White,
        strokeColor = Accent,
        strokeWidth = 2.dp
    )

    return rememberDefaultCartesianMarker(
        label = label,
        indicator = indicator,
        indicatorSize = 6.dp,
        labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
        valueFormatter = { _, targets ->
            val rawValue = targets.firstOrNull()?.let { target ->
                (target as? ColumnCartesianLayerMarkerTarget)
                    ?.columns?.firstOrNull()?.entry?.y
            } ?: 0f
            String.format(Locale.US, if (flag) "%.1f" else "%.0f", rawValue)
        }
    )
}