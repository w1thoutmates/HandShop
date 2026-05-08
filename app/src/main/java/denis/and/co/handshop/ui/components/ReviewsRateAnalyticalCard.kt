package denis.and.co.handshop.ui.components

import android.widget.ProgressBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.graphics.toColorInt
import denis.and.co.handshop.R
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.StarEmpty
import denis.and.co.handshop.ui.theme.StarFilled
import kotlin.math.abs

@Composable
fun ReviewsRateAnalyticalCard(
    seller: Seller,
    ratio: Map<Int, Int>,
    sellerForStyle: Seller
) {
    val totalReviews = seller.reviewsCount.coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(sellerForStyle.selfProfileBackground.toColorInt()), RoundedCornerShape(16.dp))
            .border(1.dp, Color(sellerForStyle.selfProfileFooterColor.toColorInt()), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val rate = seller.rate
                    for (i in 1..5) {
                        val isFilled = i <= rate.toInt()
                        Icon(
                            painter = painterResource(R.drawable.star),
                            contentDescription = null,
                            tint = if (isFilled) StarFilled else StarEmpty,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = "${String.format("%.1f", seller.rate)} / 5",
                    style = TextStyle(
                        fontFamily = Onest,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(sellerForStyle.selfProfileTextColor.toColorInt())
                    )
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(sellerForStyle.selfProfileTextColor.toColorInt()).copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 15.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (stars in 5 downTo 1) {
                    val count = ratio[stars] ?: 0
                    val progress = count.toFloat() / totalReviews

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$stars ${if (stars == 1) "звезда" else if (stars in 2..4) "звезды" else "звёзд"}",
                            style = TextStyle(
                                fontFamily = Onest,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(sellerForStyle.selfProfileTextColor.toColorInt()).copy(0.66f)
                            ),
                            modifier = Modifier.width(65.dp)
                        )

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Accent,
                            trackColor = Color(sellerForStyle.selfProfileFooterColor.toColorInt())
                        )

                        Text(
                            text = "$count ${getReviewWord(count)}",
                            style = TextStyle(
                                fontFamily = Onest,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(sellerForStyle.selfProfileTextColor.toColorInt()).copy(0.66f),
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier
                                .width(85.dp)
                                .padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getReviewWord(count: Int): String {
    val lastDigit = count % 10
    val lastTwoDigits = count % 100
    return when {
        lastTwoDigits in 11..14 -> "оценок"
        lastDigit == 1 -> "оценка"
        lastDigit in 2..4 -> "оценки"
        else -> "оценок"
    }
}