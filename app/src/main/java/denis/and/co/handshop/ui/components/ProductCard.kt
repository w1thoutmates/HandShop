package denis.and.co.handshop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
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
    imageResId: Int,
    cost: BigDecimal?,
    currency: Char?,
    rate: Int = 1,
    viewsCount: Long,
    time: String,
    city: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText)
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if(cost != null && currency != null) "$cost $currency".uppercase() else "не указана",
                        style = TextStyle(
                            fontFamily = Onest,
                            color = if(cost != null && currency != null) BlackText else LowAlphaBlackText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if(cost != null && currency != null) 16.sp else 10.sp,
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
                                modifier = Modifier.size(15.dp)
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
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            R.drawable.eye to viewsCount.toString(),
                            R.drawable.clock to time,
                            R.drawable.mark to city
                        ).forEach { (iconRes, textValue) ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(iconRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = textValue,
                                    style = TextStyle(
                                        fontFamily = Onest,
                                        color = BlackText,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp
                                    ),
                                    maxLines = if (textValue == city) 2 else 1,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListItemPreview() {
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
                painter = painterResource(R.drawable.ring_image_example),
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
                    text = "Title",
                    style = TextStyle(
                        fontFamily = Onest,
                        color = BlackText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1499 ₽".uppercase(),
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
                        val remain = 5 - 4;
                        for (i in 1..4) {
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

                Spacer(Modifier.height(4.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            R.drawable.eye to "24",
                            R.drawable.clock to "Сегодня",
                            R.drawable.mark to "Ростов-на-Дону"
                        ).forEach { (iconRes, textValue) ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(iconRes),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = textValue,
                                    style = TextStyle(
                                        fontFamily = Onest,
                                        color = BlackText,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp
                                    ),
                                    maxLines = if (textValue == "Ростов-на-Дону") 2 else 1,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}