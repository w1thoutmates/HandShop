package denis.and.co.handshop.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun Long.toRelativeDateString(): String {
    val date = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val today = LocalDate.now()

    return when {
        date == today -> "Сегодня"
        date == today.minusDays(1) -> "Вчера"
        date == today.minusDays(2) -> "Позавчера"
        else -> {
            val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
            date.format(formatter)
        }
    }
}

fun Date.formatToReadable(): String {
    val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(this)
}