package denis.and.co.handshop.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import denis.and.co.handshop.R
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack

@Preview(showBackground = true)
@Composable
fun ProductDetailsScreen() {
    Scaffold(
        topBar = { DetailsScreenHeader() },
        bottomBar = { Footer() },
        modifier = Modifier
            .background(SoftBack)
            .fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp),
                colors = CardDefaults.cardColors(containerColor = SoftBack)
            ) {
                Column( /* modifier = Modifier.fillMaxSize(), */ ) {
                    Image(
                        painter = painterResource(R.drawable.ring_image_example),
                        contentDescription = "Изображение товара",
                        alignment = Alignment.CenterStart,
                        modifier = Modifier.fillMaxWidth().height(350.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column( /* modifier = Modifier.fillMaxSize(), */ ) {
                        Text(
                            text = "1499 ₽",
                            style = TextStyle(
                                fontFamily = Onest,
                                color = BlackText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            maxLines = 1,
                            modifier = Modifier.padding(start = 15.dp, top = 10.dp, bottom = 15.dp),
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = SoftBack)
            ) {
                Column( /* modifier = Modifier.fillMaxSize(), */ ) {
                    Text(
                        text = "Название товара",
                        style = TextStyle(
                            fontFamily = Comfortaa,
                            color = BlackText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        maxLines = 1,
                        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                        overflow = TextOverflow.Ellipsis
                    )

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = LowAlphaBlackText,
                        modifier = Modifier.padding(15.dp)
                    )

                    Text(
                        text = "Описание товара лопата петрушка огород дота2 y2k джинсы опиум редан итд мемчик",
                        style = TextStyle(
                            fontFamily = Comfortaa,
                            color = LowAlphaBlackText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        ),
                        maxLines = 3,
                        modifier = Modifier.padding(start = 15.dp, bottom = 15.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = SoftBack)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Контакты",
                        style = TextStyle(
                            fontFamily = Comfortaa,
                            color = BlackText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(top = 15.dp, end = 10.dp, start = 15.dp),
                    )

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = LowAlphaBlackText,
                        modifier = Modifier.padding(15.dp)
                    )

                    Column(
                        modifier = Modifier.padding(start = 15.dp)
                    ) {
                        val contactsCount = 1

                        for(i in 1..contactsCount) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "название контакта:",
                                    style = TextStyle(
                                        fontFamily = Onest,
                                        color = BlackText,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 15.dp, end = 10.dp),
                                )

                                Text(
                                    text = "сам контакт",
                                    style = TextStyle(
                                        fontFamily = Onest,
                                        color = BlackText,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 15.dp, end = 10.dp),
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = SoftBack)
            ) {
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 15.dp, end = 10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.user_profile_avatar_mock),
                            contentDescription = "Фото профиля продавца",
                            alignment = Alignment.CenterStart,
                            modifier = Modifier
                                    .padding(end = 10.dp)
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )

                        Text(
                            text = "Имя продавца",
                            style = TextStyle(
                                fontFamily = Comfortaa,
                                color = BlackText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )

                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 30.dp, top = 5.dp, end = 10.dp, bottom = 10.dp)
                    ) {
                        Row() {
                            Image(
                                painter = painterResource(R.drawable.star_with_stroke),
                                contentDescription = "Рейтинг продавца",
                                alignment = Alignment.CenterStart,
                                modifier = Modifier.padding(end = 10.dp).size(25.dp),
                                contentScale = ContentScale.FillBounds,
                            )

                            Text(
                                text = "4.67",
                                style = TextStyle(
                                    fontFamily = Onest,
                                    color = BlackText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                ),
                                maxLines = 1,
                            )
                        }

                        Text(
                            text = "77 оценок",
                            style = TextStyle(
                                fontFamily = Onest,
                                color = LowAlphaBlackText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = SoftBack)
            ) {
                Column() {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.mark),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.size(25.dp)
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Город",
                            style = TextStyle(
                                fontFamily = Onest,
                                color = BlackText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.clock),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.size(25.dp)
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Время размещения",
                            style = TextStyle(
                                fontFamily = Onest,
                                color = BlackText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.eye),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.size(25.dp)
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "159",
                            style = TextStyle(
                                fontFamily = Onest,
                                color = BlackText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        )

                        Spacer(Modifier.width(10.dp))

                        Image(
                            painter = painterResource(R.drawable.liked_nav),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.size(25.dp)
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "53",
                            style = TextStyle(
                                fontFamily = Onest,
                                color = BlackText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }

            // карточка с блоком "похожие" ( там будет небольшая подборка товаров с такой же категорией товара )
        }
    }
}

@Composable
fun DetailsScreenHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.back_arrow_image_thin),
            contentDescription = "Кнопка назад",
            alignment = Alignment.CenterStart,
            modifier = Modifier.padding(20.dp).size(30.dp).weight(1f)
        )

        Image(
            painter = painterResource(R.drawable.like_icon),
            contentDescription = "Кнопка добавить в понравившейся",
            alignment = Alignment.CenterEnd,
            modifier = Modifier.padding(20.dp).size(35.dp)
        )
    }
}

