package denis.and.co.handshop.ui.screens.metrics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import co.yml.charts.ui.combinedchart.model.CombinedChartData
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import denis.and.co.handshop.data.enums.TimePeriod
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.viewmodel.MetricsViewModel
import java.time.format.TextStyle

@Composable
fun TotalReachMetricScreen(
    navController: NavController,
    viewModel: MetricsViewModel
) {
    var days: Int by remember { mutableIntStateOf(7) }
    val points by viewModel.reachPoints.collectAsState()

    LaunchedEffect(days) {
        viewModel.loadStats(days)
    }

    val groupBarList = points.map { point ->
        GroupBar(
            label = "День ${point.x.toInt() + 1}",
            barList = listOf(
                BarData(
                    point = point,
                    color = Accent,
                    gradientColorList = listOf(Accent, Color(0xFFFF9800)),
                    label = "День ${point.x.toInt() + 1}"
                )
            )
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(75.dp)
        .steps(points.size)
        .labelData { i -> if (i < points.size) "Д${i + 1}" else "" }
        .startDrawPadding(20.dp)
        .axisLineColor(BlackText.copy(alpha = 0.1f))
        .axisLabelColor(BlackText.copy(alpha = 0.6f))
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val max = points.maxOfOrNull { it.y } ?: 10f
            val stepValue = max / 5
            String.format("%.0f", i * stepValue)
        }
        .backgroundColor(Color.White)
        .axisLineColor(BlackText.copy(alpha = 0.1f))
        .axisLabelColor(BlackText)
        .build()

    val barStyle = BarStyle(
        isGradientEnabled = true,
        barWidth = 35.dp,
        cornerRadius = 4.dp,
        selectionHighlightData = SelectionHighlightData(
            isHighlightFullBar = true,
            groupBarPopUpLabel = { x, y ->
                "день ${x.toInt() + 1}: ${y.toInt()} показов"
            },
            highlightTextBackgroundColor = Color.Yellow,
            highlightTextColor = Color.Black
        )
    )

    val barPlotData = BarPlotData(
        groupBarList = groupBarList,
        barStyle = barStyle,
        barColorPaletteList = listOf(Accent, Color(0xFFFF9800))
    )

    val linePlotData = LinePlotData(
        lines = listOf(
            Line(
                dataPoints = points,
                lineStyle = LineStyle(color = Color(0xFFD53807), width = 4f),
                intersectionPoint = IntersectionPoint(color = Color(0xFFD53807), radius = 5.dp),
                selectionHighlightPoint = SelectionHighlightPoint(color = Color.Black),
                shadowUnderLine = ShadowUnderLine(alpha = 0.1f, color = Accent),
                selectionHighlightPopUp = SelectionHighlightPopUp(
                    popUpLabel = { x, y -> "день ${x.toInt() + 1}: ${y.toInt()} показов" },
                    backgroundColor = Color.Yellow,
                    labelColor = Color.Black
                )
            )
        )
    )

    val combinedChartData = CombinedChartData(
        combinedPlotDataList = listOf(
            barPlotData,
            linePlotData
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color.White,
    )

    Scaffold(
        containerColor = SoftBack,
        bottomBar = { AppFooter(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            HeaderSection(navController, "Охваты")

            val totalImpressions = points.sumOf { it.y.toInt() }
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Text(
                    text = "Всего показов: $totalImpressions",
                    fontFamily = Onest,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = BlackText
                )
                Text(
                    text = "Статистика по дням обновляется в реальном времени",
                    fontFamily = Onest,
                    fontSize = 14.sp,
                    color = BlackText.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimePeriod.entries.forEach { period ->
                        FilterChip(
                            label = period.label,
                            isSelected = days == period.days,
                            onClick = { days = period.days }
                        )
                    }
                }
            }

            ChartCard(points, combinedChartData)
        }
    }
}

@Composable
private fun ChartCard(points: List<Point>, combinedChartData: CombinedChartData) {
    val scrollState = rememberScrollState()
    val screenWidth = 300.dp
    val calculatedWidth = (75.dp * points.size) + 80.dp
    val finalWidth = if (calculatedWidth < screenWidth) Modifier.fillMaxWidth() else Modifier.width(calculatedWidth)

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (calculatedWidth > screenWidth) Modifier.horizontalScroll(scrollState) else Modifier)
                    .padding(top = 25.dp, bottom = 10.dp, end = 16.dp)
            ) {
                co.yml.charts.ui.combinedchart.CombinedChart(
                    modifier = Modifier
                        .then(finalWidth)
                        .fillMaxHeight(),
                    combinedChartData = combinedChartData
                )
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет данных для графика", fontFamily = Onest)
            }
        }
    }
}

@Composable
private fun HeaderSection(navController: NavController, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 15.dp).fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 15.dp)
                .size(40.dp)
                .clickable { navController.popBackStack() }
        )
        Text(
            text = title,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = Onest,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isSelected) Accent else HardBack.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = Onest,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else BlackText
            )
        )
    }
}