package denis.and.co.handshop.data.enums

enum class ComplaintStatus(val label: String) {
    PENDING("Ожидает"),
    RESOLVED("Решено"),
    REJECTED("Отклонено"),
    IGNORED("Игнорировать отправителя")
}