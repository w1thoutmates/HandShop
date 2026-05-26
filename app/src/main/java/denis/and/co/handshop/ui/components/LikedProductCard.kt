package denis.and.co.handshop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import denis.and.co.handshop.data.model.ProductWithSeller
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.StarFilled

@Composable
fun LikedProductListItem(
    item: ProductWithSeller,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onSellerClick: (String) -> Unit
) {
    val product = item.product
    val seller = item.seller

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(225.dp)
            .padding(4.dp)
            .clickable { onProductClick(product.id) },
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(125.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.LightGray)
                ) {
                    AsyncImage(
                        model = product.imageUrls.firstOrNull(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 15.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ){
                        Row(modifier = Modifier.padding(bottom = 10.dp)) {
                            Text(
                                text = "${product.cost} ₽",
                                style = TextStyle(
                                    fontFamily = Onest,
                                    color = BlackText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                ),
                                maxLines = 1
                            )

                            Text(
                                text = product.title,
                                style = TextStyle(
                                    fontFamily = Onest,
                                    color = BlackText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                ),
                                maxLines = 1,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }

                        Text(
                            text = product.description,
                            style = TextStyle(
                                fontFamily = Onest,
                                color = BlackText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                            ),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.weight(1f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.mark),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(25.dp)
                                    .padding(end = 5.dp)
                            )

                            Text(
                                text = product.targetCity,
                                style = TextStyle(
                                    fontFamily = Onest,
                                    color = BlackText,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (seller != null) {
                        Row() {
                            Row(Modifier.weight(1f)) {
                                Column(
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { onSellerClick(product.sellerId) }
                                    ) {
                                        AsyncImage(
                                            model = seller.profileImage,
                                            contentDescription = "Фото профиля",
                                            modifier = Modifier
                                                .padding(end = 10.dp)
                                                .size(35.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                        )

                                        Text(
                                            text = seller.sellerName,
                                            style = TextStyle(
                                                fontFamily = Comfortaa,
                                                color = BlackText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(R.drawable.star),
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 5.dp).size(16.dp),
                                            colorFilter = ColorFilter.tint(StarFilled)
                                        )
                                        Text(
                                            text = String.format("%.2f", seller.rate),
                                            style = TextStyle(fontFamily = Onest, fontSize = 14.sp)
                                        )
                                        Text(
                                            text = " • ${seller.reviewsCount} оценок",
                                            style = TextStyle(
                                                fontFamily = Onest,
                                                color = LowAlphaBlackText,
                                                fontSize = 12.sp
                                            ),
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(end = 16.dp, start = 16.dp, top = 30.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Удалить",
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clickable {
                                            onDeleteClick()
                                        },
                                    tint = Color.Gray
                                )

                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Поделиться",
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clickable { onShareClick() },
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}