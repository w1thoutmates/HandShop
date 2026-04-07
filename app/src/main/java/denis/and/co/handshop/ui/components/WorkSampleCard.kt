package denis.and.co.handshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import denis.and.co.handshop.data.model.WorkSample
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.WhiteText

@Composable
fun WorkSampleCard(workSample: WorkSample) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        val imageUrl = workSample.imageUrls.firstOrNull()

        AsyncImage(
            model = imageUrl,
            contentDescription = workSample.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(HardBack)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = workSample.title,
                    color = WhiteText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Comfortaa,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                if (workSample.description.isNotBlank()) {
                    Text(
                        text = workSample.description,
                        color = WhiteText.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 2,
                        fontFamily = Comfortaa,
                        overflow = TextOverflow.Ellipsis
                        // добавить возмонжость развернуть карточку чтобы подробнее почитать описание кейса
                    )
                }
            }
        }
    }
}