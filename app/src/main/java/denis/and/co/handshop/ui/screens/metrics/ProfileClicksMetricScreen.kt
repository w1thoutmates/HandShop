package denis.and.co.handshop.ui.screens.metrics

import androidx.compose.foundation.layout.*
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
import co.yml.charts.common.model.Point
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shape.Shape
import denis.and.co.handshop.data.enums.TimePeriod
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.viewmodel.MetricsViewModel
import denis.and.co.handshop.viewmodel.ProfileViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProfileClicksMetricScreen(
    navController: NavController,
    viewModel: MetricsViewModel,
    profileViewModel: ProfileViewModel
) {
    var days by remember { mutableIntStateOf(7) }

    val points by viewModel.profileClickPoints.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val seller by profileViewModel.seller.collectAsState()

    val formatter = remember {
        DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
    }

    val modelProducer = remember {
        CartesianChartModelProducer.build()
    }

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(null)
    }

    LaunchedEffect(days) {
        viewModel.loadProfileClicks(days)
    }

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            modelProducer.tryRunTransaction {
                lineSeries {
                    series(points.map(Point::y))
                }
            }
        }
    }

    seller?.let { currentSeller ->

        Scaffold(
            containerColor = Color(currentSeller.selfProfileBackground.toColorInt()),
            bottomBar = {
                AppFooter(navController, currentSeller)
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {

                HeaderSection(
                    navController = navController,
                    title = "Просмотры профиля",
                    seller = currentSeller
                )

                val totalClicks = points.sumOf { it.y.toInt() }

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {

                    Text(
                        text = "Всего просмотров: $totalClicks",
                        fontFamily = Onest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt())
                    )

                    Text(
                        text = "Количество переходов в профиль продавца",
                        fontFamily = Onest,
                        fontSize = 14.sp,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt())
                            .copy(alpha = 0.5f)
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
                                onClick = {
                                    days = period.days
                                },
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
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    if (points.isNotEmpty()) {

                        CartesianChartHost(
                            chart = rememberCartesianChart(

                                rememberLineCartesianLayer(
                                    lines = listOf(
                                        rememberLineSpec(
                                            shader = com.patrykandpatrick.vico.core.common.shader.DynamicShader.color(
                                                Accent
                                            ),
                                            thickness = 3.dp,
                                            point = rememberShapeComponent(
                                                shape = Shape.Pill,
                                                color = Accent
                                            ),
                                            pointSize = 8.dp
                                        )
                                    )
                                ),

                                startAxis = rememberStartAxis(
                                    itemPlacer = AxisItemPlacer.Vertical.step(step = { 1f })
                                ),

                                bottomAxis = rememberBottomAxis(
                                    valueFormatter = { value, _, _ ->
                                        stats.getOrNull(value.toInt())?.let {
                                            try {
                                                LocalDate.parse(it.date)
                                                    .format(formatter)
                                            } catch (ex: Exception) {
                                                ""
                                            }
                                        } ?: ""
                                    },
                                ),
                            ),
//                            marker = rememberDefaultCartesianMarker(
//                                label = rememberTextComponent(
//                                    color = Color.White,
//                                    background = rememberShapeComponent(
//                                        color = Accent,
//                                        shape = Shape.rounded(6.dp)
//                                    ),
//                                    padding = Dimensions.of(
//                                        horizontal = 8.dp,
//                                        vertical = 4.dp
//                                    ),
//                                    textSize = 14.sp,
//                                    textAlignment = Layout.Alignment.ALIGN_CENTER
//                                )
//                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )

                    } else {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "Нет данных",
                                fontFamily = Onest
                            )
                        }
                    }
                }
            }
        }
    }
}