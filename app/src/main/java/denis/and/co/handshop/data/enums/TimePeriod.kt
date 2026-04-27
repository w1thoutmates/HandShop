package denis.and.co.handshop.data.enums

enum class TimePeriod(val label: String, val days: Int) {
    Week("Неделя", 7),
    Month("Месяц", 30),
    All("Все время", 365)
}