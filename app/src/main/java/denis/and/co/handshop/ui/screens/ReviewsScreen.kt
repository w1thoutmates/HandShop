package denis.and.co.handshop.ui.screens

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import denis.and.co.handshop.R
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.StarEmpty
import denis.and.co.handshop.ui.theme.StarFilled

@Preview
@Composable
fun ReviewsScreen() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBack)
    ) { padding ->
        var sortOption by remember { mutableStateOf("Сначала новые") };

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Column(modifier = Modifier.padding(padding)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 25.dp).fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .size(40.dp)
                    )

                    Text(
                        text = "Оценки",
                        style = TextStyle(
                            fontFamily = Comfortaa,
                            color = BlackText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = when (sortOption) {
                            "С высокой оценкой" -> {
                                Icons.Outlined.KeyboardArrowDown
                            }

                            "С низкой оценкой" -> {
                                Icons.Outlined.KeyboardArrowUp
                            }

                            else -> {
                                Icons.Outlined.AddCircle // заменить на картинку стрелок вверх и вниз
                            }
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .size(35.dp)
                            .clickable {
                                /*
                                    открытие меню сортировки
                                */
                            }
                    )

                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 25.dp).fillMaxWidth()
                ) {
                    Text(
                        text = "4.96",
                        style = TextStyle(
                            fontFamily = Onest,
                            color = BlackText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        ),
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
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

                        Text(
                            text = "Отзывы продавца",
                            style = TextStyle(
                                fontFamily = Onest,
                                color = LowAlphaBlackText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            ),
                        )

                    }

                }
            }
        }

    }
}