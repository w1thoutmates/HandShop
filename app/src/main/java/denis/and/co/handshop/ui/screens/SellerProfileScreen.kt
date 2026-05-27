package denis.and.co.handshop.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import android.widget.Toast
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Shield
import denis.and.co.handshop.ui.components.ReportDialog
import denis.and.co.handshop.ui.navigation.ModeratorRoute
import denis.and.co.handshop.viewmodel.ComplaintViewModel
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import denis.and.co.handshop.R
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.components.ProductListItem
import denis.and.co.handshop.ui.components.WorkSampleCard
import denis.and.co.handshop.ui.navigation.EditProductRoute
import denis.and.co.handshop.ui.navigation.EditProfileRoute
import denis.and.co.handshop.ui.navigation.ExpandedPublishedProductsRoute
import denis.and.co.handshop.ui.navigation.LoginRoute
import denis.and.co.handshop.ui.navigation.MetricsRoute
import denis.and.co.handshop.ui.navigation.ProductDetailsRoute
import denis.and.co.handshop.ui.navigation.ReviewsRoute
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.StarEmpty
import denis.and.co.handshop.ui.theme.StarFilled
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.viewmodel.AuthViewModel
import denis.and.co.handshop.viewmodel.LikedViewModel
import denis.and.co.handshop.viewmodel.MetricsViewModel
import denis.and.co.handshop.viewmodel.ProfileViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SellerProfileScreen(
    navController: NavController,
    sellerId: String?,
    viewModel: ProfileViewModel = viewModel(),
    likedViewModel: LikedViewModel,
    metricsViewModel: MetricsViewModel,
    authViewModel: AuthViewModel,
    complaintViewModel: ComplaintViewModel
) {
    val seller by viewModel.seller.collectAsState()
    val isMyProfile = sellerId == null || sellerId == viewModel.currentUid
    val sellerProducts by viewModel.sellerProducts.collectAsState()

    var showReportDialog by remember { mutableStateOf(false) }
    val isSendingComplaint by complaintViewModel.isSending.collectAsState()
    val complaintResult by complaintViewModel.sendResult.collectAsState()

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(sellerId) {
        viewModel.loadProfile(sellerId)
    }

    LaunchedEffect(complaintResult) {
        complaintResult?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            complaintViewModel.clearResult()
        }
    }

    seller?.let {currentSeller ->
        val pagerState = rememberPagerState(pageCount = { currentSeller.workSamples.size })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(currentSeller.selfProfileBackground.toColorInt()))
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
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
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { navController.popBackStack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Кнопка назад",
                                    tint = Color(currentSeller.selfProfileIconsColor.toColorInt()),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            if (isMyProfile) {
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .clickable { showMenu = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Кнопка с опциями для продавца",
                                            tint = Color(currentSeller.selfProfileIconsColor.toColorInt()),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Выйти из аккаунта",
                                                    fontFamily = Comfortaa
                                                )
                                            },
                                            onClick = {
                                                showMenu = false
                                                authViewModel.signOut(context) {
                                                    navController.navigate(LoginRoute) {
                                                        popUpTo(0) { inclusive = true }
                                                        launchSingleTop = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable { showReportDialog = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Flag,
                                        contentDescription = "Пожаловаться",
                                        tint = Color(0xFFE53935).copy(alpha = 0.7f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            if (isMyProfile && currentSeller.role == "moderator") {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable { navController.navigate(ModeratorRoute) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Shield,
                                        contentDescription = "Модерация",
                                        tint = Color(currentSeller.selfProfileIconsColor.toColorInt()),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
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
                            color = Color(currentSeller.selfProfileTextColor.toColorInt()),
                            fontFamily = Comfortaa,
                            modifier = Modifier.weight(1f)
                        )
                        if (isMyProfile) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = Color(currentSeller.selfProfileIconsColor.toColorInt()),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        navController.navigate(EditProfileRoute)
                                    }
                            )
                        }
                    }

                    if (currentSeller.isBanned) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE53935))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "⛔ Аккаунт заблокирован",
                                fontFamily = Onest,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentSeller.description,
                        fontSize = 14.sp,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt()).copy(alpha = 0.66f),
                        lineHeight = 20.sp,
                        fontFamily = Comfortaa
                    )
                }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = BlackText.copy(alpha = 0.2f),
                    modifier = Modifier.padding(15.dp)
                )

                if (!currentSeller.isBanned) {
                    ContactInfoBlock(currentSeller.contacts, currentSeller, context, viewModel)
                }

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
                        color = Color(currentSeller.selfProfileTextColor.toColorInt()),
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

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = BlackText.copy(alpha = 0.2f),
                        modifier = Modifier.padding(15.dp)
                    )
                }

                if (isMyProfile) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable {
                                navController.navigate(MetricsRoute(currentSeller.id))
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Метрики для продавца",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(currentSeller.selfProfileTextColor.toColorInt()),
                            fontFamily = Comfortaa
                        )

                        Spacer(Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = Color(currentSeller.selfProfileIconsColor.toColorInt())
                        )
                    }

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = BlackText.copy(alpha = 0.2f),
                        modifier = Modifier.padding(15.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable {
                            navController.navigate(ExpandedPublishedProductsRoute(currentSeller.id))
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Опубликованные товары ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt()),
                        fontFamily = Comfortaa
                    )

                    Text(
                        text = "(${viewModel.sellerProducts.value.count()})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt()).copy(alpha = 0.66f),
                        fontFamily = Comfortaa
                    )

                    Spacer(Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp),
                        tint = Color(currentSeller.selfProfileIconsColor.toColorInt())
                    )
                }

                val visibleProducts = if (isMyProfile) {
                    sellerProducts
                } else {
                    sellerProducts.filter {
                        it?.status == ProductStatus.ACTIVE
                        || it?.status == ProductStatus.SOLD
                    }
                }

                if (visibleProducts.isEmpty()) {
                    Text(
                        text = "Нет товаров",
                        fontFamily = Comfortaa,
                        color = Color(currentSeller.selfProfileTextColor.toColorInt()).copy(alpha = 0.66f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(visibleProducts) { product ->
                            val isInactive = product?.status != ProductStatus.ACTIVE

                            if (product != null) {
                                Box(
                                    modifier = Modifier
                                        .alpha(if (isInactive) 0.5f else 1f)
                                        .width(200.dp)
                                ) {
                                    ProductListItem(
                                        product = product,
                                        onClick = {
                                            if (isMyProfile) {
                                                navController.navigate(EditProductRoute(product.id))
                                            } else {
                                                navController.navigate(ProductDetailsRoute(product.id))
                                                metricsViewModel.updateProductClickStat(product)
                                            }
                                        },
                                        seller = seller,
                                        viewModel = likedViewModel,
                                        isMyProfile = isMyProfile
                                    )

                                    if (isInactive) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(top = 15.dp, end = 20.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(BlackText.copy(0.75f))
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = product.status.value,
                                                style = TextStyle(
                                                    fontFamily = Onest,
                                                    color = when {
                                                        product.status == ProductStatus.HIDDEN -> WhiteText
                                                        product.status == ProductStatus.SOLD -> Color.Green
                                                        (product.status == ProductStatus.SOLD) && !isMyProfile -> Color.Red
                                                        else -> BlackText
                                                    },
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                RatingBlock(currentSeller, navController)

                Button(
                    onClick = {
                        navController.navigate(ReviewsRoute(currentSeller.id))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(currentSeller.selfProfileAccentColor.toColorInt())),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Перейти к отзывам",
                        fontFamily = Comfortaa,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(currentSeller.selfProfileAccentTextColor.toColorInt())
                    )
                }

            }

            AppFooter(navController, currentSeller)
        }

        if (showReportDialog) {
            ReportDialog(
                sellerName = currentSeller.sellerName,
                isSending = isSendingComplaint,
                onDismiss = { showReportDialog = false },
                onSend = { text ->
                    complaintViewModel.sendComplaint(currentSeller, text)
                    showReportDialog = false
                }
            )
        }
    } ?: Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center), color = Accent) }
}

@Composable
fun RatingBlock(seller: Seller, navController: NavController) {
    var count by remember { mutableIntStateOf(seller.reviewsCount) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < seller.rate.toInt()) StarFilled else StarEmpty,
                modifier = Modifier.size(25.dp)
            )
        }
        Text(
            text = "${seller.rate.let { String.format("%.2f", it) }} ($count отзывов)",
            fontSize = 18.sp,
            fontFamily = Comfortaa,
            color = Color(seller.selfProfileTextColor.toColorInt()).copy(alpha = 0.66f),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ContactInfoBlock(
    contacts: Map<String, String>,
    seller: Seller,
    context: Context,
    profileViewModel: ProfileViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    showDialog = true
                    profileViewModel.updateCountClicksOnContacts(seller.id)
                    profileViewModel.updateCountClickStat(seller.id)
                }
        ) {
            Text(
                text = "Связаться с мастером",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(seller.selfProfileTextColor.toColorInt()),
                fontFamily = Comfortaa,
                modifier = Modifier.padding(bottom = 8.dp).weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp),
                tint = Color(seller.selfProfileIconsColor.toColorInt())
            )
        }

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
                    Text(text = type, fontSize = 10.sp, color = Color(seller.selfProfileTextColor.toColorInt()).copy(alpha = 0.66f), fontFamily = Comfortaa)
                    Text(text = value, fontSize = 14.sp, color = Color(seller.selfProfileTextColor.toColorInt()), fontFamily = Comfortaa)
                }
            }
        }
    }

    if(showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(25.dp),
            containerColor = WhiteText,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Выберите удобный способ связи с продавцом",
                        fontFamily = Onest,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = BlackText,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(40.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Accent.copy(alpha = 0.4f))
                    )
                }
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(seller.contacts.entries.toList()) { contact ->

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        when (contact.key) {
                                            "Номер телефона" -> openDial(context, contact.value)
                                            "Почта" -> openEmail(context, contact.value)
                                            "Телеграм" -> openTelegramChat(
                                                context,
                                                contact.value.removePrefix("@")
                                            )
                                        }

                                        showDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = contact.key,
                                    fontFamily = Comfortaa,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = BlackText,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false },
                    modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = "Закрыть",
                        fontFamily = Comfortaa,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Accent
                    )
                }
            }
        )
    }
}

fun openTelegramChat(context: Context, username: String) {
    val url = "tg://resolve?domain=$username"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    intent.setPackage("org.telegram.messenger")

    try {
        context.startActivity(intent)
    } catch (ex: Exception) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me"))
        context.startActivity(browserIntent)
    }
}

fun openDial(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phone")

    context.startActivity(intent)
}

fun openEmail(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:$email")

    context.startActivity(intent)
}