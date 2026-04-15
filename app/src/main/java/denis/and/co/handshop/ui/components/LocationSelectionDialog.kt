package denis.and.co.handshop.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import denis.and.co.handshop.data.model.LocationProvider
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.WhiteText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionDialog(
    onDismissRequest: () -> Unit,
    onLocationSelected: (String) -> Unit,
    currentLocation: String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(25.dp),
        containerColor = WhiteText,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Выберите город",
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
                    items(LocationProvider.locations) { city ->
                        val isSelected = city == currentLocation

                        val backgroundColor by animateColorAsState(
                            targetValue = if (isSelected) Accent.copy(alpha = 0.15f) else Color.Transparent,
                            animationSpec = tween(durationMillis = 300),
                            label = "itemBg"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(backgroundColor)
                                .clickable {
                                    if (city == "Другое")
                                        onLocationSelected("")
                                    else
                                        onLocationSelected(city)
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                text = city,
                                fontFamily = Comfortaa,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = BlackText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
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