package denis.and.co.handshop.ui.screens.metrics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import denis.and.co.handshop.data.enums.TimePeriod
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.viewmodel.ReviewsViewModel
import kotlin.random.Random

@Composable
fun SellerRateMetricScreen(
    navController: NavController,
    viewModel: ReviewsViewModel
) {
    val points by viewModel.ratingPoints.collectAsState()
    val seller by viewModel.seller.collectAsState()

    val barData = points.map { point ->
        BarData(
            point = point,
            color = Accent,
            label = "Отзыв ${point.x.toInt() + 1}",
            gradientColorList = listOf(
                Accent,
                Color(0xFFD53807)
            )
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(70.dp)
        .steps(barData.size)
        .labelData { i -> if (i < barData.size) "${i + 1}" else "" }
        .axisLabelAngle(0f)
        .axisLineColor(BlackText.copy(alpha = 0.1f))
        .axisLabelColor(BlackText.copy(alpha = 0.6f))
        .startDrawPadding(20.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i -> i.toString() }
        .axisLineColor(BlackText.copy(alpha = 0.1f))
        .axisLabelColor(BlackText)
        .backgroundColor(Color.White)
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color.White,
        showYAxis = true,
        showXAxis = true,
        horizontalExtraSpace = 15.dp,
        barStyle = BarStyle(
            isGradientEnabled = true,
            barBlendMode = BlendMode.SrcOver,
        )
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
                    text = "Рейтинг продавца",
                    style = TextStyle(fontFamily = Onest, fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Текущий рейтинг: ${String.format("%.1f", seller?.rate ?: 0.0)}",
                    fontFamily = Onest,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BlackText
                )
                Text(
                    text = "На основе ${points.size} отзывов",
                    fontFamily = Onest,
                    fontSize = 14.sp,
                    color = BlackText.copy(alpha = 0.5f)
                )
            }

            val scrollState = rememberScrollState()
            val chartWidth = (70.dp * barData.size) + 60.dp

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                if (barData.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(top = 20.dp, end = 15.dp)
                            .horizontalScroll(scrollState)
                    ) {
                        co.yml.charts.ui.barchart.BarChart(
                            modifier = Modifier
                                .width(chartWidth)
                                .fillMaxHeight(),
                            barChartData = barChartData
                        )
                    }
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Нет отзывов для отображения", fontFamily = Onest)
                    }
                }
            }
        }
    }
}