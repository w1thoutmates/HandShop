package denis.and.co.handshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import denis.and.co.handshop.R

val Comfortaa = FontFamily(Font(R.font.Comfortaa))
val Onest = FontFamily(Font(R.font.Onest))
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Comfortaa,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Comfortaa,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Onest,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp
    )
)