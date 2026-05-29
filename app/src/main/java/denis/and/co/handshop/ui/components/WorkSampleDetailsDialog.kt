package denis.and.co.handshop.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.data.model.WorkSample
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkSampleDetailsDialog(
    workSample: WorkSample,
    onDismiss: () -> Unit,
    seller: Seller
) {
    seller?.let { currentSeller ->
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            val softBack = Color(currentSeller.selfProfileBackground.toColorInt())
            val hardBack = Color(currentSeller.selfProfileFooterColor.toColorInt())
            val textColor = Color(currentSeller.selfProfileTextColor.toColorInt())
            val iconColor = Color(currentSeller.selfProfileIconsColor.toColorInt())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(softBack)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    val images = workSample.imageUrls
                    if (images.isNotEmpty()) {
                        val pagerState = rememberPagerState(pageCount = { images.size })

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = images[page],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .align(Alignment.BottomCenter)
                            )

                            if (images.size > 1) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 16.dp)
                                ) {
                                    images.forEachIndexed { index, _ ->
                                        val isSelected = pagerState.currentPage == index
                                        Box(
                                            modifier = Modifier
                                                .size(
                                                    width = if (isSelected) 20.dp else 6.dp,
                                                    height = 6.dp
                                                )
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) textColor
                                                    else textColor.copy(alpha = 0.25f)
                                                )
                                        )
                                        if (index < images.size - 1) Spacer(Modifier.width(4.dp))
                                    }
                                }
                            }

                            if (images.size > 1) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(start = 16.dp, bottom = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {

                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = workSample.title,
                            fontFamily = Comfortaa,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = textColor,
                            lineHeight = 30.sp
                        )

                        if (workSample.description.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(hardBack)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = workSample.description,
                                    fontFamily = Onest,
                                    fontSize = 15.sp,
                                    color = textColor.copy(alpha = 0.75f),
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        if (workSample.imageUrls.size > 1) {
                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = "Все фотографии (${workSample.imageUrls.size})",
                                fontFamily = Comfortaa,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = textColor.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            val chunkedImages = workSample.imageUrls.chunked(2)
                            chunkedImages.forEach { rowImages ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    rowImages.forEach { url ->
                                        AsyncImage(
                                            model = url,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                    }
                                    if (rowImages.size == 1) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(80.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(textColor.copy(alpha = 0.12f))
                        .clickable { onDismiss() }
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Закрыть",
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    } ?: Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center), color = Accent) }
}