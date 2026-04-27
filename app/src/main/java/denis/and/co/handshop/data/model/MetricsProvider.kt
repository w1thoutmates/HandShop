package denis.and.co.handshop.data.model

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
                    title = "Рейтинг продавца", // статистика: за все время / за месяц / за неделю
                    route = SellerRateMetricRoute(sellerId)
                ),
                ChildItem(
                    id = "gm_child_2",
                    title = "Суммарный охват",
                    description = "Статистика, которая показывает, сколько раз пользователям показывались объявления продавца",
                    route = TotalReachMetricRoute
                ),
                ChildItem(
                    id = "gm_child_3",
                    title = "Соотношение категорий опубликованных товаров", // круговая диаграмма, например 70% Дерево, 13% Аксессуары и тд
                    route = ProductCategoryRationMetricRoute
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
                    route = AddedToLikedMetricRoute
                ),
                ChildItem(
                    id = "cpm_child_2",
                    title = "Коэффициент кликабельности (CTR)",
                    description = "Статистика, которая показывает соотношение того, сколько раз объявление показывалось пользователям и сколько раз по этому объявлению пользователь кликнул",
                    route = CTRMetricRoute
                ),
                ChildItem(
                    id = "cpm_child_3",
                    title = "Индекс конкурентной цены",
                    description = "Статистика, которая представляет собой сравнение цен конкурентов, ориентируясь на похожие товары",
                    route = CompetitorsCostCompareMetricRoute
                ),
                ChildItem(
                    id = "cpm_child_4",
                    title = "Клики по кнопке «связаться»",
                    route = ClicksOnContactsMetricRoute
                )
            )
        )
    )
}