package denis.and.co.handshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import denis.and.co.handshop.R
import denis.and.co.handshop.ui.theme.*

@Composable
fun RecommendationScreen() {

}

@Preview(showBackground = true)
@Composable
fun RecommendationScreenPreview() {
    Scaffold(
        topBar = { Header() },
        bottomBar = { Footer() },
        modifier = Modifier
        .background(SoftBack)
        .fillMaxSize()
    ) {
        Content()
    }
}

@Composable
fun Header() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))
            .padding(0.dp)
            .background(HardBack)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(top = 5.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.app_icon),
                contentDescription = "Логотип приложения",
                Modifier.padding(start = 25.dp, end = 30.dp, bottom = 5.dp).size(100.dp, 50.dp),
                contentScale = ContentScale.FillBounds
            )

            Button(
                onClick = { /* catalog open fun + catalog icon anim. */ },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = WhiteText),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.catalog_icon),
                    contentDescription = "Каталог",
                    Modifier.padding(start = 0.dp, end = 7.dp).size(20.dp),
                    contentScale = ContentScale.FillBounds
                )

                Text(text = "Каталог", style = Typography.labelSmall)

            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 40.dp)
        ) {
            var input by remember { mutableStateOf("") }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(45.dp)
                    .dropShadow(
                        shape = RoundedCornerShape(15.dp),
                        shadow = Shadow(
                            radius = 5.dp,
                            offset = DpOffset(x = 0.dp, y = 3.dp),
                            alpha = 0.35f
                        )
                    )
            ) {
                TextField(
                    value = input,
                    onValueChange = { newValue -> input = newValue },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 2.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = BlackText,
                        unfocusedTextColor = GreyText,
                        focusedContainerColor = WhiteText,
                        unfocusedContainerColor = WhiteText,
                        focusedIndicatorColor =  Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = GreyText,
                        unfocusedLabelColor = GreyText
                    ),
                    placeholder = {
                        Text(
                            "Найти в Ручной Лавке",
                            style = TextStyle(
                                fontFamily = Comfortaa,
                                color = Color(0x66000000),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                        )
                    },
                    shape = RoundedCornerShape(14.dp)
                )

                Button(
                    onClick = { /* search button. */ },
                    shape = RoundedCornerShape(0.dp, 14.dp, 14.dp, 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = WhiteText),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding()
                        .width(50.dp)
                    ,
                ) {
                    Image(
                        painterResource(R.drawable.search_icon),
                        contentDescription = "Иконка поиска",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Content() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {

    }
}

@Composable
fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
            .background(HardBack),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.padding(top = 17.dp, bottom = 17.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {   },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape
            ) {
                Image(
                    painter = painterResource(R.drawable.home_nav),
                    contentDescription = "Домой навигация",
                    modifier = Modifier.size(30.dp, 30.dp),
                    contentScale = ContentScale.FillBounds
                )
            }

//            Spacer(Modifier.width(45.dp))

            Button(
                onClick = {   },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape
            ) {
                Image(
                    painter = painterResource(R.drawable.search_nav),
                    contentDescription = "Поиск навигация",
                    modifier = Modifier.size(30.dp, 30.dp),
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.5f
                )
            }

//            Spacer(Modifier.width(45.dp))

            Button(
                onClick = {   },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape
            ) {
                Image(
                    painter = painterResource(R.drawable.cart_nav),
                    contentDescription = "Корзина навигация",
                    modifier = Modifier.size(30.dp, 30.dp),
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.5f
                )
            }

//            Spacer(Modifier.width(45.dp))

            Button(
                onClick = {   },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape
            ) {
                Image(
                    painter = painterResource(R.drawable.liked_nav),
                    contentDescription = "Избранное навигация",
                    modifier = Modifier.size(30.dp, 30.dp),
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.5f
                )
            }

//            Spacer(Modifier.width(45.dp))

            Button(
                onClick = {   },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape
            ) {
                Image(
                    painter = painterResource(R.drawable.profile_nav),
                    contentDescription = "Профиль навигация",
                    modifier = Modifier.size(30.dp, 30.dp),
                    contentScale = ContentScale.FillBounds,
                    alpha = 0.5f
                )
            }
        }
    }
}