package denis.and.co.handshop.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}

fun generateColor(index: Int): Color {
    val hue = (index * 137.5f) % 360f
    val saturation = 0.65f
    val value = 0.95f

    return Color.hsv(hue, saturation, value)
}

fun getColorForIndex(index: Int, basePalette: List<Color>): Color {
    return if (index < basePalette.size) {
        basePalette[index]
    } else {
        generateColor(index)
    }
}

fun getContrastColor(color: Color): Color {
    return if (color.luminance() > 0.5) {
        Color.Black
    } else {
        Color.White
    }
}