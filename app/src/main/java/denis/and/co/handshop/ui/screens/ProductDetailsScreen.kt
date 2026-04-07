package denis.and.co.handshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import denis.and.co.handshop.R
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.model.WorkSample
import denis.and.co.handshop.data.toRelativeDateString
import denis.and.co.handshop.ui.navigation.ProfileRoute
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.viewmodel.ProductDetailsVM
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ProductDetailsScreen(
    product: Product,
    viewModel: ProductDetailsVM,
    navController: NavController
) {
    val seller by viewModel.seller.collectAsState()

    if (seller == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Accent)
        }
        return
    }

    val currentSeller = seller!!

    Scaffold(
        bottomBar = { Footer(navController) },
        modifier = Modifier
            .background(SoftBack)
            .fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftBack)
                ) {
                    Column( /* modifier = Modifier.fillMaxSize(), */ ) {
                        val pagerState = rememberPagerState(pageCount = { product.imageUrls.count() })
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth().height(350.dp)
                        ) { page ->
                            AsyncImage(
                                model = product.imageUrls[page],
                                contentDescription = "Изображение товара",
                                alignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column( /* modifier = Modifier.fillMaxSize(), */ ) {
                            Text(
                                text = product.cost.toString() + " " + product.currency.toString(),
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
                            text = product.title,
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
                            text = product.description,
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
                            modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                        ) {
                            currentSeller.contacts.forEach { (type, value) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = when {
                                            type.contains("Почта") -> Icons.Outlined.Email
                                            type.contains("Телеграм") -> Icons.Outlined.Send
                                            else -> Icons.Outlined.Phone
                                        },
                                        contentDescription = null,
                                        tint = BlackText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column(modifier = Modifier.padding(start = 12.dp)) {
                                        Text(text = type, fontSize = 13.sp, color = LowAlphaBlackText, fontFamily = Comfortaa)
                                        Text(text = value, fontSize = 17.sp, color = BlackText, fontFamily = Comfortaa)
                                    }
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
                                .clickable {
                                    navController.navigate(ProfileRoute(sellerId = product.sellerId))
                                }
                        ) {
                            AsyncImage(
                                model = currentSeller.profileImage,
                                contentDescription = "Фото профиля продавца",
                                alignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(50.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )

                            Text(
                                text = currentSeller.sellerName,
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
                                    text = currentSeller.rate.toString(),
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
                                text = currentSeller.reviewsCount.toString() + " оценок",
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
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(25.dp),
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = product.targetCity,
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
                                text = product.postedTime.toRelativeDateString(),
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
                                text = product.viewsCount.toString(),
                                style = TextStyle(
                                    fontFamily = Onest,
                                    color = BlackText,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )

                            Spacer(Modifier.width(10.dp))

                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(25.dp),
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = product.addedToLikedCount.toString(),
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
                Spacer(modifier = Modifier.height(16.dp))
                // карточка с блоком "похожие" ( там будет небольшая подборка товаров с такой же категорией товара )
            }
            DetailsScreenHeader(navController)
        }
    }
}

@Composable
fun DetailsScreenHeader(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Кнопка назад",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { navController.popBackStack() },
                tint = BlackText,
            )
        }

        Box(
            modifier = Modifier
                .padding(20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "Кнопка добавить в понравившейся",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { /* TODO: */ }
            )
        }
    }
}

