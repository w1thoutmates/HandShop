package denis.and.co.handshop.data.model

import denis.and.co.handshop.R

object CategoryProvider {
    val categories = listOf(
        Category("Украшения и аксессуары", R.drawable.diamond_icon),
        Category("Дом и интерьер", R.drawable.furniture_icon),
        Category("Одежда и обувь", R.drawable.clothes_icon),
        Category("Игрушки и товары для детей", R.drawable.plush_toys_icon),
        Category("Подарки и праздники", R.drawable.gift_icon),
        Category("Другое", R.drawable.three_dots_icon)
    )

}