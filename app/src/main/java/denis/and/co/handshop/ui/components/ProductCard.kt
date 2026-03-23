package denis.and.co.handshop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.shadow.ShadowContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import denis.and.co.handshop.R
import denis.and.co.handshop.ui.theme.*
import java.math.BigDecimal

@Composable
fun ProductListItem(
    title: String,
    description: String,
    imageResId: Int,
    cost: BigDecimal,
    currency: Char,
    rate: Int = Math.clamp(1, 1, 5)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(

        ) {
            Image(
                painter = painterResource(imageResId),
                contentDescription = "Изображение в карточке товара",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Column(
                Modifier.padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = Onest,
                        color = BlackText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = description,
                    style = TextStyle(
                        fontFamily = Onest,
                        color = LowAlphaBlackText,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$cost $currency".uppercase(),
                        style = TextStyle(
                            fontFamily = Onest,
                            color = BlackText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        val remain = 5 - rate;
                        for (i in 1..rate) {
                            Image(
                                painter = painterResource(
                                    R.drawable.star
                                ),
                                contentDescription = "Закрашенная звездочка",
                                colorFilter = ColorFilter.tint(StarFilled),
                                modifier = Modifier
                                    .size(15.dp)
                                    .dropShadow(
                                        shape = CircleShape,
                                        shadow = Shadow(
                                            radius = 4.dp,
                                            offset = DpOffset(x = 0.dp, y = 1.dp),
                                            alpha = 0.15f
                                        )
                                    )
                            )
                        }
                        if (remain != 0) {
                            for(i in 1..remain) {
                                Image(
                                    painter = painterResource(
                                        R.drawable.star
                                    ),
                                    contentDescription = "Не закрашенная звездочка",
                                    colorFilter = ColorFilter.tint(StarEmpty),
                                    modifier = Modifier
                                        .size(15.dp)
                                        .dropShadow(
                                            shape = CircleShape,
                                            shadow = Shadow(
                                                radius = 4.dp,
                                                offset = DpOffset(x = 0.dp, y = 1.dp),
                                                alpha = 0.15f
                                            )
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