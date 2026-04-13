package denis.and.co.handshop.data.enums

enum class ProductStatus(val value: String = "Активный") {
    ACTIVE("Активный"),
    HIDDEN("Скрытый"),
    SOLD("Продан")
}