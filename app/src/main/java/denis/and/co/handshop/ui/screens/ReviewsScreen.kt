package denis.and.co.handshop.ui.screens

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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

        ReviewsScreenContent(padding)

    }
}


@Composable
fun ReviewsScreenContent(modifier: PaddingValues) {
    var sortOption by remember { mutableStateOf("Сначала новые") };

    Column(Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp),
            colors = CardDefaults.cardColors(containerColor = SoftBack)
        ) {
            Box(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Column(modifier = Modifier.padding(modifier)) {
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

                        Image(
                            painter = when (sortOption) {
                                "С высокой оценкой" -> {
                                    painterResource(R.drawable.sort_by_desc)
                                }

                                "С низкой оценкой" -> {
                                    painterResource(R.drawable.sort_by_asc)
                                }

                                else -> {
                                    painterResource(R.drawable.sort_by_news)
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
                        modifier = Modifier.padding(top = 25.dp, bottom = 15.dp).fillMaxWidth()
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
                            modifier = Modifier.padding(start = 25.dp)
                        )

                        Column(
                            modifier = Modifier.padding(start = 15.dp).weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val rate = 4.37 ?: 0.0
                                for (i in 1..5) {
                                    val isFilled = i <= rate
                                    Image(
                                        painter = if (isFilled)
                                            painterResource(R.drawable.star_with_stroke)
                                        else
                                            painterResource(R.drawable.star),
                                        contentDescription = if (isFilled) "Закрашенная" else "Пустая",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Оценки продавца",
                                style = TextStyle(
                                    fontFamily = Comfortaa,
                                    color = LowAlphaBlackText,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                ),
                            )

                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .size(40.dp)
                                .clickable {
                                    /*
                                открытие меню со статистикой по оценкам
                                5шт прогресс баров с соотношением поставленных оценок
                                например 5 - 100 оценок, 4 - 30 оценок, 3 - 0 оценок,
                                2 - 10 оценок, 1 - 30 оценок
                            */
                                },
                            tint = BlackText.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp),
            colors = CardDefaults.cardColors(containerColor = SoftBack)
        ) {
            // блок с полем ввода отзыва
        }

        LazyColumn() {
            // список отзывов
        }

    }
}