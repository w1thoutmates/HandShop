package denis.and.co.handshop.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowSizeClass
import coil.compose.AsyncImage
import denis.and.co.handshop.data.model.WorkSample
import denis.and.co.handshop.data.repository.ImageRepository
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.WhiteText
import kotlinx.coroutines.launch

@Composable
fun AddWorkSampleDialog(
    imageRepo: ImageRepository,
    existingSample: WorkSample? = null,
    onDismiss: () -> Unit,
    onAdd: (WorkSample) -> Unit
) {
    val isEditMode = existingSample != null

    var title by remember { mutableStateOf(existingSample?.title ?: "") }
    var description by remember { mutableStateOf(existingSample?.description ?: "") }

    var existingUrls by remember { mutableStateOf(existingSample?.imageUrls ?: emptyList()) }
    var newLocalUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        val totalAllowed = 5 - existingUrls.size
        if (totalAllowed > 0) {
            newLocalUris = (newLocalUris + uris).take(totalAllowed)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .background(SoftBack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isEditMode) "Редактировать работу" else "Добавить работу",
                        fontFamily = Comfortaa,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = BlackText,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(HardBack)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Закрыть",
                            tint = BlackText.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(
                            "Название работы *",
                            fontFamily = Comfortaa,
                            color = LowAlphaBlackText
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = Comfortaa, color = BlackText, fontSize = 15.sp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = LowAlphaBlackText.copy(alpha = 0.4f),
                        cursorColor = Accent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            "Описание",
                            fontFamily = Comfortaa,
                            color = LowAlphaBlackText
                        )
                    },
                    singleLine = false,
                    textStyle = TextStyle(fontFamily = Comfortaa, color = BlackText, fontSize = 15.sp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = LowAlphaBlackText.copy(alpha = 0.4f),
                        cursorColor = Accent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Фотографии",
                        fontFamily = Comfortaa,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = BlackText
                    )
                    Spacer(Modifier.weight(1f))
                    val totalCount = existingUrls.size + newLocalUris.size
                    Text(
                        text = "$totalCount / 5",
                        fontFamily = Onest,
                        fontSize = 13.sp,
                        color = if (totalCount >= 5) Accent else BlackText.copy(alpha = 0.4f)
                    )
                }

                val totalImages = existingUrls.size + newLocalUris.size
                val canAddMore = totalImages < 5

                val displayItems: List<ImageItem> = buildList {
                    existingUrls.forEach { add(ImageItem.Remote(it)) }
                    newLocalUris.forEach { add(ImageItem.Local(it)) }
                    if (canAddMore) add(ImageItem.AddButton)
                }

                val gridHeight = ((displayItems.size + 1) / 2 * 110 + 10).dp

                val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

                val columns = when {
                    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> 4
                    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> 3
                    else -> 2
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gridHeight),
                    userScrollEnabled = true
                ) {
                    items(displayItems) { item ->
                        when (item) {
                            is ImageItem.AddButton -> {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(
                                            width = 1.5.dp,
                                            color = Accent.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .background(Accent.copy(alpha = 0.07f))
                                        .clickable {
                                            imagePickerLauncher.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Add,
                                            contentDescription = "Добавить фото",
                                            tint = Accent,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Text(
                                            text = "Добавить",
                                            fontFamily = Comfortaa,
                                            fontSize = 11.sp,
                                            color = Accent
                                        )
                                    }
                                }
                            }

                            is ImageItem.Remote -> {
                                ImageCell(
                                    model = item.url,
                                    onRemove = { existingUrls = existingUrls - item.url }
                                )
                            }

                            is ImageItem.Local -> {
                                ImageCell(
                                    model = item.uri,
                                    onRemove = { newLocalUris = newLocalUris - item.uri }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Отмена",
                            fontFamily = Comfortaa,
                            fontWeight = FontWeight.SemiBold,
                            color = BlackText.copy(alpha = 0.55f)
                        )
                    }

                    Button(
                        enabled = !isUploading && title.isNotBlank(),
                        onClick = {
                            scope.launch {
                                isUploading = true
                                val uploadedNewUrls = imageRepo.uploadProductImages(newLocalUris)
                                val finalUrls = existingUrls + uploadedNewUrls

                                onAdd(
                                    WorkSample(
                                        id = existingSample?.id ?: java.util.UUID.randomUUID().toString(),
                                        title = title.trim(),
                                        description = description.trim(),
                                        imageUrls = finalUrls
                                    )
                                )

                                isUploading = false
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                color = WhiteText,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                if (isEditMode) "Сохранить" else "Добавить",
                                fontFamily = Comfortaa,
                                fontWeight = FontWeight.Bold,
                                color = WhiteText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageCell(
    model: Any?,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
    ) {
        AsyncImage(
            model = model,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(26.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Удалить",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

private sealed class ImageItem {
    object AddButton : ImageItem()
    data class Remote(val url: String) : ImageItem()
    data class Local(val uri: Uri) : ImageItem()
}