package denis.and.co.handshop.ui.screens.metrics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import denis.and.co.handshop.data.enums.TimePeriod
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.viewmodel.MetricsViewModel
import denis.and.co.handshop.viewmodel.ProfileViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ClicksOnContactsMetricScreen(
    navController: NavController,
    viewModel: MetricsViewModel,
    profileViewModel: ProfileViewModel
) {
    var days by remember { mutableIntStateOf(7) }
    val points by viewModel.clickPoints.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val seller by profileViewModel.seller.collectAsState()

    val modelProducer = remember { CartesianChartModelProducer.build() }
    val formatter = remember { DateTimeFormatter.ofPattern("d MMM", Locale("ru")) }
    val marker = rememberMarker(flag = false)

    LaunchedEffect(days) {
        viewModel.loadContactClicks(days)
    }

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            modelProducer.tryRunTransaction {
                columnSeries { series(points.map { it.y }) }
            }
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(null)
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
            ) {
                HeaderSection(navController, "Запросы контактов", currentSeller)

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Text(
                        text = "Всего кликов: ${currentSeller.countClicksOnContacts}",
                        fontFamily = Onest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt())
                    )
                    Text(
                        text = "Как часто покупатели открывали ваши контакты",
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
                        .height(400.dp)
                        .padding(16.dp),
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
                                    )
                                ),
                                startAxis = rememberStartAxis(
                                    label = rememberAxisLabelComponent(color = BlackText, textSize = 12.sp),
                                    guideline = rememberLineComponent(BlackText.copy(0.1f)),
                                    itemPlacer = AxisItemPlacer.Vertical.step(step = { 1f })
                                ),
                                bottomAxis = rememberBottomAxis(
                                    label = rememberAxisLabelComponent(
                                        color = BlackText.copy(alpha = 0.6f),
                                        textSize = 10.sp
                                    ),
                                    valueFormatter = { value, _, _ ->
                                        stats.getOrNull(value.toInt())?.let {
                                            try { LocalDate.parse(it.date).format(formatter) } catch (ex: Exception) { "" }
                                        } ?: ""
                                    }
                                )
                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            marker = marker
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Данные собираются...", fontFamily = Onest)
                        }
                    }
                }
            }
        }
    }
}