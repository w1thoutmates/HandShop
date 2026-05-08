package denis.and.co.handshop.ui.screens.metrics

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import co.yml.charts.ui.piechart.models.PieChartData.Slice
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.utils.getColorForIndex
import denis.and.co.handshop.viewmodel.ProfileViewModel

@Composable
fun ProductCategoryRationMetricScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val seller by viewModel.seller.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile(null)
    }

    seller?.let { currentSeller ->
        val basePalette = listOf(
            Color(0xFF6C5CE7),
            Color(0xFF00B894),
            Color(0xFFFF7675),
            Color(0xFFFDCB6E),
            Color(0xFF0984E3),
            Color(0xFFE17055)
        )

        val products by viewModel.sellerProducts.collectAsState()
        val categories = products
            .filterNotNull()
            .groupingBy { it.category.trim() }
            .eachCount()

        val pieData = categories.entries.mapIndexed { index, entry ->
            Slice(
                label = entry.key,
                value = entry.value.toFloat(),
                color = getColorForIndex(index, basePalette)
            )
        }

        if (pieData.size == 1) {
            Text(
                text = "Все товары в одной категории: ${pieData.first().label}",
                fontFamily = Onest,
                color = Color(currentSeller.selfProfileTextColor.toColorInt())
            )
        }

        val pieChartData = PieChartData(
            slices = pieData,
            plotType = PlotType.Pie
        )

        val pieChartConfig = PieChartConfig(
            isSumVisible = true,
            isAnimationEnable = true,
            showSliceLabels = false,
            animationDuration = 1500
        )

        val total = pieData.sumOf { it.value.toDouble() }

        Scaffold(
            containerColor = Color(currentSeller.selfProfileBackground.toColorInt()),
            bottomBar = { AppFooter(navController, currentSeller) }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {

                HeaderSection(navController, "Категории товаров", currentSeller)

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Распределение по категориям",
                        fontFamily = Onest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt())
                    )

                    Text(
                        text = "Доля каждой категории от общего числа",
                        fontFamily = Onest,
                        fontSize = 14.sp,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt()).copy(alpha = 0.5f)
                    )
                }

                ChartCard(pieChartData, pieChartConfig)

                Legend(pieData, total)
            }
        }
    }
}

@Composable
private fun ChartCard(
    pieChartData: PieChartData,
    pieChartConfig: PieChartConfig
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        if (pieChartData.slices.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    modifier = Modifier.fillMaxWidth(),
                    pieChartData = pieChartData,
                    pieChartConfig = pieChartConfig
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет данных",
                    fontFamily = Onest,
                    color = BlackText.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun Legend(
    data: List<Slice>,
    total: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        data.forEach { slice ->
            val percent = (slice.value / total * 100).toInt()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .background(slice.color, RoundedCornerShape(4.dp))
                        .height(14.dp)
                        .fillMaxWidth(0.05f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${slice.label} — $percent%",
                        fontFamily = Onest,
                        fontSize = 14.sp,
                        color = BlackText
                    )

                    val count = slice.value.toInt()

                    Text(
                        text = "($count ${if (count == 1) "объявление" else if (count in 2..4) "объявления" else "объявлений"})",
                        fontFamily = Onest,
                        fontSize = 14.sp,
                        color = LowAlphaBlackText,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}