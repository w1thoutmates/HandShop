package denis.and.co.handshop.data.model

import denis.and.co.handshop.data.enums.ProductStatus
import java.math.BigDecimal
import com.fasterxml.uuid.Generators

data class Product(
    val id: String = Generators.timeBasedEpochGenerator().generate().toString(),
    val title: String = "Название товара не указано",
    val description: String = "Описание не указано",
    val imageUrls: List<Int> = emptyList(), // заменить на List<String>, сюда будут попадать url картинок из firebase storage
    val cost: BigDecimal? = null,
    val currency: String? = "₽",
    val rate: Double = 0.0,
    val viewsCount: Long = 0,
    val postedTime: Long = System.currentTimeMillis(), // обрабатывать и превращать в "Сегодня"/"Вчера"/"21 марта" и тд.
    val targetCity: String = "Город не указан",
    val category: String = "Категория не указана",
    val sellerId: String = "",
    val status: ProductStatus = ProductStatus.ACTIVE
) {

    init {
        require(rate in 0.0..5.0) { "Рейтинг не может быть ниже 0 и больше 5. Получен рейтинг: $rate" }
    }

}