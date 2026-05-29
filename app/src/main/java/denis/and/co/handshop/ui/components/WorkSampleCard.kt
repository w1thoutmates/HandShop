package denis.and.co.handshop.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import denis.and.co.handshop.data.model.WorkSample
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.Onest

@Composable
fun WorkSampleCard(
    workSample: WorkSample,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "card_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1A1A2E))
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ) else Modifier
            )
    ) {
        val imageUrl = workSample.imageUrls.firstOrNull()
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = workSample.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.45f to Color.Transparent,
                            0.72f to Color(0xFF000000).copy(alpha = 0.55f),
                            1.0f to Color(0xFF000000).copy(alpha = 0.88f)
                        )
                    )
                )
        )

        if (workSample.imageUrls.size > 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${workSample.imageUrls.size}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontFamily = Onest,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = workSample.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                fontFamily = Comfortaa,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (workSample.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = workSample.description,
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    fontFamily = Onest,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

            if (workSample.imageUrls.size > 1) {
                Spacer(Modifier.height(10.dp))
                Row {
                    workSample.imageUrls.take(5).forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (index == 0) 18.dp else 6.dp, 4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == 0) Color.White
                                    else Color.White.copy(alpha = 0.35f)
                                )
                        )
                        if (index < workSample.imageUrls.size - 1) {
                            Spacer(Modifier.width(3.dp))
                        }
                    }
                }
            }
        }
    }
}