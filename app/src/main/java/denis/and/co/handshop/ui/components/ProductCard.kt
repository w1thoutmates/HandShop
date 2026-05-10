package denis.and.co.handshop.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import denis.and.co.handshop.R
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.utils.toRelativeDateString
import denis.and.co.handshop.ui.theme.*
import denis.and.co.handshop.viewmodel.CatalogViewModel
import denis.and.co.handshop.viewmodel.LikedViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductListItem(
    product: Product,
    onClick: () -> Unit,
    seller: Seller?,
    viewModel: LikedViewModel,
    isMyProfile: Boolean = false,
    catalogViewModel: CatalogViewModel? = null
) {

    var isInLiked by remember { mutableStateOf<Boolean?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(product.id) {
        isInLiked = viewModel.isProductExistInLiked(product.id)
    }

    if (catalogViewModel != null) {
        LaunchedEffect(Unit) {
            catalogViewModel.registerImpressionOnSession(product)
        }
    }

    val displayState = isInLiked ?: false

    Card(
        modifier = Modifier
//            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(Modifier.fillMaxSize()) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = "Изображение в карточке товара",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
//                    .height(120.dp),
                    error = painterResource(R.drawable.error_picture)
                )

                if (!isMyProfile) {
                    Box(
                        modifier = Modifier
                            .padding(15.dp)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        AnimatedContent(
                            targetState = displayState,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                        fadeOut(animationSpec = tween(300))
                            }
                        ) { liked ->
                            Icon(
                                imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (liked) "Удалить из избранного" else "Добавить в избранное",
                                modifier = Modifier
                                    .size(35.dp)
                                    .clickable {
                                        scope.launch {
                                            isInLiked = !liked
                                            try {
                                                if (liked) {
                                                    viewModel.deleteFromLiked(product.id)
                                                } else {
                                                    viewModel.addToLiked(product.id, product.sellerId)
                                                }
                                            } catch (ex: Exception) {
                                                isInLiked = liked
                                            }
                                        }
                                    },
                                tint = BlackText
                            )
                        }
                    }
                }
            }

            Column(
                Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.title,
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
                        text = if (product.cost != null && product.currency != null) "${product.cost} ${product.currency}".uppercase() else "не указана",
                        style = TextStyle(
                            fontFamily = Onest,
                            color = if (product.cost != null && product.currency != null) BlackText else LowAlphaBlackText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (product.cost != null && product.currency != null) 16.sp else 10.sp,
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
                        val rate = seller?.rate ?: 0.0
                        for (i in 1..5) {
                            val isFilled = i <= rate
                            Image(
                                painter = painterResource(R.drawable.star),
                                contentDescription = if (isFilled) "Закрашенная" else "Пустая",
                                colorFilter = ColorFilter.tint(if (isFilled) StarFilled else StarEmpty),
                                modifier = Modifier.size(15.dp)
                            )
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
                            R.drawable.eye to product.viewsCount.toString(),
                            R.drawable.clock to product.postedTime.toRelativeDateString(),
                            R.drawable.mark to product.targetCity
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
                                    maxLines = 1,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}