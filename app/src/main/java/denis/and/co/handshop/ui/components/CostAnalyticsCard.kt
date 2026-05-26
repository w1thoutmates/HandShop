package denis.and.co.handshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest

@Composable
fun CostAnalyticsCard(
    text: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, icon, title) = when (text) {
        "Цена выше рынка" -> Triple(
            Color(0x1AFF7373),
            Icons.Default.ThumbDown,
            "Выше рынка"
        )

        "Цена ниже рынка" -> Triple(
            Color(0x1A65BD5E),
            Icons.Default.ThumbUp,
            "Ниже рынка"
        )

        "Средняя цена по рынку" -> Triple(
            Color(0x1AFFDD00),
            Icons.Default.Remove,
            "Средняя цена"
        )

        else -> Triple(
            Color(0x10000000),
            Icons.Default.Info,
            "Аналитика"
        )
    }

    val accentColor = when (text) {
        "Цена выше рынка" -> Color(0xFFFF7373)
        "Цена ниже рынка" -> Color(0xFF65BD5E)
        "Средняя цена по рынку" -> Color(0xFFFFB700)
        else -> LowAlphaBlackText
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = title,
                    fontFamily = Onest,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BlackText
                )

                Text(
                    text = "На основе похожих товаров",
                    fontFamily = Onest,
                    fontSize = 12.sp,
                    color = LowAlphaBlackText
                )
            }

            var showInfo by remember { mutableStateOf(false) }


            Spacer(Modifier.weight(1f))

            Box {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { showInfo = true },
                    tint = LowAlphaBlackText
                )

                if (showInfo) {
                    Popup(
                        alignment = Alignment.TopEnd,
                        onDismissRequest = { showInfo = false }
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Аналитика цены",
                                    style = TextStyle(
                                        fontFamily = Onest,
                                        color = BlackText,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                )

                                HorizontalDivider(
                                    thickness = 2.dp,
                                    color = BlackText.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(5.dp)
                                )

                                Text(
                                    text = "Аналитика основана на категории, названии и поисковых триграммных индексах",
                                    style = TextStyle(
                                        fontFamily = Comfortaa,
                                        color = BlackText,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}