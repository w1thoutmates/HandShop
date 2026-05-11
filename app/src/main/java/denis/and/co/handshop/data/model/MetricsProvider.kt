package denis.and.co.handshop.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.StarRate
import denis.and.co.handshop.ui.navigation.AddedToLikedMetricRoute
import denis.and.co.handshop.ui.navigation.CTRMetricRoute
import denis.and.co.handshop.ui.navigation.ClicksOnContactsMetricRoute
import denis.and.co.handshop.ui.navigation.CompetitorsCostCompareMetricRoute
import denis.and.co.handshop.ui.navigation.ProductCategoryRationMetricRoute
import denis.and.co.handshop.ui.navigation.SellerRateMetricRoute
import denis.and.co.handshop.ui.navigation.TotalReachMetricRoute

object MetricsProvider {
    fun getItems(sellerId: String) = listOf(
        ParentItem(
            id = "global_metrics",
            title = "Общие метрики",
            children = listOf(
                ChildItem(
                    id = "gm_child_1",
                    title = "Рейтинг продавца",
                    route = SellerRateMetricRoute(sellerId),
                    icon = Icons.Default.StarRate
                ),
                ChildItem(
                    id = "gm_child_2",
                    title = "Суммарный охват",
                    description = "Статистика, которая показывает, сколько раз пользователям показывались объявления продавца",
                    route = TotalReachMetricRoute,
                    icon = Icons.Default.RemoveRedEye
                ),
                ChildItem(
                    id = "gm_child_3",
                    title = "Соотношение категорий опубликованных товаров",
                    route = ProductCategoryRationMetricRoute,
                    icon = Icons.Default.Category
                ),
                ChildItem(
                    id = "gm_child_4",
                    title = "Клики по кнопке «связаться»",
                    route = ClicksOnContactsMetricRoute,
                    icon = Icons.Default.AddIcCall
                )
            )
        ),

        ParentItem(
            id = "concrete_product_metrics",
            title = "Метрики по конкретным товарам",
            children = listOf(
                ChildItem(
                    id = "cpm_child_1",
                    title = "Добавлено в избранное",
                    route = AddedToLikedMetricRoute(sellerId),
                    icon = Icons.Default.Favorite
                ),
                ChildItem(
                    id = "cpm_child_2",
                    title = "Коэффициент кликабельности (CTR)",
                    description = "Статистика, которая показывает соотношение того, сколько раз объявление показывалось пользователям и сколько раз по этому объявлению пользователь кликнул",
                    route = CTRMetricRoute(sellerId),
                    icon = Icons.Default.AdsClick
                ),
                ChildItem(
                    id = "cpm_child_3",
                    title = "Индекс конкурентной цены",
                    description = "Статистика, которая представляет собой сравнение цен конкурентов, ориентируясь на похожие товары",
                    route = CompetitorsCostCompareMetricRoute(sellerId),
                    icon = Icons.Default.BarChart
                )
            )
        )
    )
}