package denis.and.co.handshop.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import denis.and.co.handshop.R
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.model.WorkSample
import denis.and.co.handshop.ui.components.ProductListItem
import denis.and.co.handshop.ui.components.WorkSampleCard
import denis.and.co.handshop.ui.navigation.EditProductRoute
import denis.and.co.handshop.ui.navigation.ProductDetailsRoute
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.viewmodel.ProfileViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SellerProfileScreen(
    navController: NavController,
    sellerId: String?,
    viewModel: ProfileViewModel = viewModel()
) {
    val seller by viewModel.seller.collectAsState()
    val isMyProfile = sellerId == null || sellerId == viewModel.currentUid
    val sellerProducts by viewModel.sellerProducts.collectAsState()

    LaunchedEffect(sellerId) {
        viewModel.loadProfile(sellerId)
    }

    seller?.let {currentSeller ->
        val pagerState = rememberPagerState(pageCount = { currentSeller.workSamples.size })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftBack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = currentSeller.coverImageUrl,
                    contentDescription = "Обложка профиля",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color.LightGray)
                )
                AsyncImage(
                    model = currentSeller.profileImage.ifEmpty { painterResource(R.drawable.user_profile_avatar_mock) },
                    contentDescription = "Аватар продавца",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = 16.dp, y = 0.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Box(modifier = Modifier
                    .padding(top = 40.dp, start = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Кнопка назад",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentSeller.sellerName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText,
                        fontFamily = Comfortaa,
                        modifier = Modifier.weight(1f)
                    )
                    if (isMyProfile) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = BlackText,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    navController.navigate("edit_profile")
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentSeller.description,
                    fontSize = 14.sp,
                    color = LowAlphaBlackText,
                    lineHeight = 20.sp,
                    fontFamily = Comfortaa
                )
            }

            HorizontalDivider(
                thickness = 2.dp,
                color = BlackText.copy(alpha = 0.2f),
                modifier = Modifier.padding(15.dp)
            )

            ContactInfoBlock(currentSeller.contacts)

            HorizontalDivider(
                thickness = 2.dp,
                color = BlackText.copy(alpha = 0.2f),
                modifier = Modifier.padding(15.dp)
            )

            if (currentSeller.workSamples.isNotEmpty()) {
                Text(
                    text = "Портфолио",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BlackText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontFamily = Comfortaa
                )

                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier.padding(top = 10.dp).fillMaxWidth()
                ) { page ->
                    WorkSampleCard(workSample = currentSeller.workSamples[page])
                }
            }

            HorizontalDivider(thickness = 2.dp, color = BlackText.copy(alpha = 0.2f), modifier = Modifier.padding(15.dp))

            Text(
                text = "Опубликованные товары",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlackText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontFamily = Comfortaa
            )

            val visibleProducts = if (isMyProfile) {
                sellerProducts
            } else {
                sellerProducts.filter { it?.status == ProductStatus.ACTIVE }
            }

            if (visibleProducts.isEmpty()) {
                Text(
                    text = "Нет товаров",
                    fontFamily = Comfortaa,
                    color = LowAlphaBlackText,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(visibleProducts) { product ->
                        val isInactive = product?.status != ProductStatus.ACTIVE

                        if (product != null) {
                            Box(modifier = Modifier.alpha(if (isInactive) 0.5f else 1f)) {
                                ProductListItem(
                                    product = product,
                                    onClick = {
                                        if (isMyProfile) {
                                            navController.navigate(EditProductRoute(product.id))
                                        } else {
                                            navController.navigate(ProductDetailsRoute(product.id))
                                        }
                                    },
                                    seller = seller
                                )
                            }
                        }
                    }
                }
            }

            RatingBlock(currentSeller.rate, currentSeller.reviewsCount)

            // Список отзывов и переместить кнопку "Оценить"

        }
    } ?: Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
}

@Composable
fun RatingBlock(rate: Double, reviewsCount: Int) {
    var count by remember { mutableIntStateOf(reviewsCount) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < rate.toInt()) Color(0xFFFFB400) else Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = "$rate ($count отзывов)", // переделать на то как это реализовано в product details
            fontSize = 14.sp,
            fontFamily = Comfortaa,
            color = LowAlphaBlackText,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Оценить",
            fontSize = 12.sp,
            color = WhiteText,
            fontWeight = FontWeight.Bold,
            fontFamily = Comfortaa,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Accent)
                .clickable { count++ }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ContactInfoBlock(contacts: Map<String, String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Связаться с мастером",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = BlackText,
            fontFamily = Comfortaa,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        contacts.forEach { (type, value) ->
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
                    Text(text = type, fontSize = 10.sp, color = LowAlphaBlackText, fontFamily = Comfortaa)
                    Text(text = value, fontSize = 14.sp, color = BlackText, fontFamily = Comfortaa)
                }
            }
        }
    }
}