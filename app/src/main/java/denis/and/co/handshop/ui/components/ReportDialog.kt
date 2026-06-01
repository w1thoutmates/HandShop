package denis.and.co.handshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.WhiteText

@Composable
fun ReportDialog(
    sellerName: String,
    isSending: Boolean,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val isValid = text.trim().length >= 10

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = WhiteText,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Пожаловаться",
                    fontFamily = Onest,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = BlackText,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "на «$sellerName»",
                    fontFamily = Comfortaa,
                    fontSize = 14.sp,
                    color = LowAlphaBlackText,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(40.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE53935))
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Опишите нарушение",
                    fontFamily = Comfortaa,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = BlackText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= 500) text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    placeholder = {
                        Text(
                            "Например: мошенничество ...",
                            fontFamily = Comfortaa,
                            fontSize = 13.sp,
                            color = LowAlphaBlackText,
                            lineHeight = 18.sp
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE53935),
                        unfocusedBorderColor = LowAlphaBlackText.copy(alpha = 0.3f),
                        cursorColor = Color(0xFFE53935)
                    ),
                    textStyle = TextStyle(
                        fontFamily = Comfortaa,
                        fontSize = 14.sp,
                        color = BlackText
                    ),
                    singleLine = false
                )

                Text(
                    text = "${text.length}/500",
                    fontFamily = Comfortaa,
                    fontSize = 11.sp,
                    color = LowAlphaBlackText.copy(0.5f),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )

                if (!isValid && text.isNotBlank()) {
                    Text(
                        text = "Минимум 10 символов",
                        fontFamily = Comfortaa,
                        fontSize = 12.sp,
                        color = Color(0xFFE53935),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (!isSending && isValid) onSend(text) },
                enabled = isValid && !isSending,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    disabledContainerColor = Color(0xFFE53935).copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .height(16.dp)
                            .width(16.dp),
                        strokeWidth = 2.dp,
                        color = WhiteText
                    )
                } else {
                    Text(
                        "Отправить",
                        fontFamily = Comfortaa,
                        fontWeight = FontWeight.Bold,
                        color = WhiteText
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    "Отмена",
                    fontFamily = Comfortaa,
                    color = LowAlphaBlackText
                )
            }
        }
    )
}