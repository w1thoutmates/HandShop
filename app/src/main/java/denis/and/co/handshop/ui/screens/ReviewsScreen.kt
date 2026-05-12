package denis.and.co.handshop.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import denis.and.co.handshop.R
import denis.and.co.handshop.data.model.Review
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.ui.components.ReviewsRateAnalyticalCard
import denis.and.co.handshop.ui.navigation.ProfileRoute
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.StarEmpty
import denis.and.co.handshop.ui.theme.StarFilled
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.utils.getContrastColor
import denis.and.co.handshop.viewmodel.ProfileViewModel
import denis.and.co.handshop.viewmodel.ReviewsViewModel

@Composable
fun ReviewsScreen(
    viewModel: ReviewsViewModel = viewModel(),
    navController: NavController,
    profileViewModel: ProfileViewModel,
    sellerId: String
) {
    LaunchedEffect(Unit) {
        viewModel.fixCurrentSellerRating()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBack)
    ) {
        ReviewsScreenContent(
            modifier = PaddingValues(0.dp),
            viewModel = viewModel,
            navController = navController,
            profileViewModel = profileViewModel,
            sellerId = sellerId
        )
    }
}


@Composable
fun ReviewsScreenContent(
    modifier: PaddingValues,
    viewModel: ReviewsViewModel,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    sellerId: String
) {
    val seller by viewModel.seller.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    val ratio = remember(reviews) {
        reviews.groupBy { it.selectedRate }
            .mapValues { it.value.size }
    }

    var sortOption by remember { mutableStateOf("Сначала новые") };
    var showSortMenu by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    var selectedRate by remember { mutableStateOf(0) }
    var showPopup by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val sellerForStyling by profileViewModel.seller.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(sellerId)
    }

    sellerForStyling?.let { currentSeller ->
        Column(Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp),
                colors = CardDefaults.cardColors(containerColor = Color(currentSeller.selfProfileBackground.toColorInt()))
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
                                    .clickable {
                                        navController.popBackStack()
                                    },
                                tint = Color(currentSeller.selfProfileIconsColor.toColorInt())
                            )

                            Text(
                                text = "Оценки",
                                style = TextStyle(
                                    fontFamily = Onest,
                                    color = Color(currentSeller.selfProfileTextColor.toColorInt()),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Box {
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
                                            showSortMenu = true
                                        },
                                    colorFilter = ColorFilter.tint(Color(currentSeller.selfProfileIconsColor.toColorInt()))
                                )

                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false },
                                    modifier = Modifier
                                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(15.dp))
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(Color.White, RoundedCornerShape(15.dp)),
                                    containerColor = Color.White,
                                    shape = RoundedCornerShape(15.dp),
                                    tonalElevation = 0.dp,
                                    shadowElevation = 0.dp,
                                    offset = DpOffset(x = (-16).dp, y = 0.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Image(
                                                    painter = painterResource(R.drawable.sort_by_news),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    text = "Сначала новые",
                                                    style = TextStyle(
                                                        fontFamily = Comfortaa,
                                                        fontSize = 14.sp,
                                                        color = if (sortOption == "Сначала новые") BlackText else LowAlphaBlackText
                                                    ),
                                                    modifier = Modifier.padding(start = 12.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            sortOption = "Сначала новые"
                                            showSortMenu = false
                                            viewModel.loadReviewsSortedByGreaterDate()
                                        },

                                        )

                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Image(
                                                    painter = painterResource(R.drawable.sort_by_desc),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    text = "С высокой оценкой",
                                                    style = TextStyle(
                                                        fontFamily = Comfortaa,
                                                        fontSize = 14.sp,
                                                        color = if (sortOption == "С высокой оценкой") BlackText else LowAlphaBlackText
                                                    ),
                                                    modifier = Modifier.padding(start = 12.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            sortOption = "С высокой оценкой"
                                            showSortMenu = false
                                            viewModel.loadReviewsSortedByDesc()
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Image(
                                                    painter = painterResource(R.drawable.sort_by_asc),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    text = "С низкой оценкой",
                                                    style = TextStyle(
                                                        fontFamily = Comfortaa,
                                                        fontSize = 14.sp,
                                                        color = if (sortOption == "С низкой оценкой") BlackText else LowAlphaBlackText
                                                    ),
                                                    modifier = Modifier.padding(start = 12.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            sortOption = "С низкой оценкой"
                                            showSortMenu = false
                                            viewModel.loadReviewsSortedByAsc()
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 25.dp, bottom = 15.dp).fillMaxWidth()
                        ) {
                            Text(
                                text = seller?.rate?.let { String.format("%.2f", it) } ?: "0.00",
                                style = TextStyle(
                                    fontFamily = Onest,
                                    color = Color(currentSeller.selfProfileTextColor.toColorInt()),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(start = 25.dp)
                            )

                            Column(
                                modifier = Modifier.padding(start = 15.dp).weight(1f)
                            ) {
                                val currentRate = seller?.rate ?: 0.0
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    for (i in 1..5) {
                                        Icon(
                                            painter = painterResource(R.drawable.star),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (i <= currentRate.toInt()) StarFilled else StarEmpty
                                        )
                                    }
                                }

                                Text(
                                    text = "Оценки продавца",
                                    style = TextStyle(
                                        fontFamily = Comfortaa,
                                        color = Color(currentSeller.selfProfileTextColor.toColorInt()).copy(0.66f),
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
                                        showPopup = true
                                    },
                                tint = Color(currentSeller.selfProfileIconsColor.toColorInt()).copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (showPopup && seller != null) {
                        Popup(
                            alignment = Alignment.Center,
                            onDismissRequest = { showPopup = false }
                        ) {
                            Box(
                                Modifier
                                    .padding(horizontal = 20.dp)
                                    .shadow(5.dp, RoundedCornerShape(16.dp))
                            ) {
                                ReviewsRateAnalyticalCard(
                                    seller = seller ?: return@Popup,
                                    ratio = ratio,
                                    sellerForStyle = currentSeller
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color(currentSeller.selfProfileBackground.toColorInt()))
            ) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    val isFormValid = !input.trim().isEmpty() && selectedRate > 0

                    Text(
                        text = "Оценка и комментарии",
                        style = TextStyle(
                            fontFamily = Comfortaa,
                            color = Color(currentSeller.selfProfileTextColor.toColorInt()),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        maxLines = 1,
                        modifier = Modifier.padding(top = 15.dp, start = 16.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 10.dp, start = 16.dp)
                    ) {
                        for (i in 1..5) {
                            Icon(
                                painter = painterResource(R.drawable.star),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(35.dp)
                                    .clickable {
                                        selectedRate = i
                                    },
                                tint = if (i <= selectedRate) StarFilled else StarEmpty
                            )
                        }
                    }

                    ReviewTextField(
                        value = input,
                        onValueChange = { input = it },
                        label = "Поделитесь впечатлением о товаре",
                        singleLine = false,
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 16.dp, end = 16.dp),
                        seller = currentSeller
                    )

                    Button(
                        onClick = {
                            if (isFormValid) {
                                viewModel.postReview(input, selectedRate)
                                input = ""
                                selectedRate = 0
                                Toast.makeText(context, "Отзыв успешно опубликован", Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(currentSeller.selfProfileAccentColor.toColorInt())),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Опубликовать отзыв",
                            fontFamily = Comfortaa,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (!isFormValid) {
                                Color(currentSeller.selfProfileTextColor.toColorInt()).copy(0.66f)
                            } else
                            {
                                getContrastColor(Color(currentSeller.selfProfileTextColor.toColorInt()))
                            }
                        )
                    }
                }
            }

            val reviews by viewModel.reviews.collectAsState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(reviews) { review ->
                    ReviewItem(
                        review = review,
                        navController = navController,
                        seller = currentSeller
                    )
                }
            }

        }
    }
}

@Composable
fun ReviewTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth(),
    seller: Seller
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = Comfortaa, color = Color(seller.selfProfileTextColor.toColorInt()).copy(0.66f)) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(fontFamily = Comfortaa, color = Color(seller.selfProfileTextColor.toColorInt()), fontSize = 16.sp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(seller.selfProfileAccentColor.toColorInt()),
            unfocusedBorderColor = Color(seller.selfProfileTextColor.toColorInt()).copy(alpha = 0.5f),
            cursorColor = Color(seller.selfProfileAccentColor.toColorInt())
        ),
        modifier = modifier,
    )
}

@Composable
fun ReviewItem(
    review: Review,
    navController: NavController,
    seller: Seller
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(seller.selfProfileBackground.toColorInt())),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(bottom = 7.dp)
                    .clickable {
                        navController.navigate(ProfileRoute(review.reviewerId))
                    }
            ) {
                AsyncImage(
                    model = review.reviewerAvatar.ifEmpty { painterResource(R.drawable.user_profile_avatar_mock) },
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                )
                Text(
                    text = review.reviewerName,
                    style = TextStyle(
                        fontFamily = Comfortaa,
                        color = Color(seller.selfProfileTextColor.toColorInt()),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        painter = painterResource(R.drawable.star),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (index < review.selectedRate) StarFilled else StarEmpty
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Отзыв",
                    style = TextStyle(fontFamily = Comfortaa, fontSize = 12.sp, color = Color(seller.selfProfileTextColor.toColorInt()).copy(0.66f))
                )
            }

            Text(
                text = review.text,
                modifier = Modifier.padding(top = 8.dp),
                style = TextStyle(fontFamily = Onest, fontSize = 15.sp, color = Color(seller.selfProfileTextColor.toColorInt()))
            )
        }
    }
}