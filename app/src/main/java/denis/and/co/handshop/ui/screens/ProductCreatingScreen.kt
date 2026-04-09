package denis.and.co.handshop.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.ui.theme.*
import denis.and.co.handshop.viewmodel.CreateProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreatingScreen(
    navController: NavController,
    viewModel: CreateProductViewModel,
    initialProduct: Product? = null,
) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var title by remember { mutableStateOf(initialProduct?.title ?: "") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var cost by remember { mutableStateOf(initialProduct?.cost?.toString() ?: "") }
    var targetCity by remember { mutableStateOf(initialProduct?.targetCity ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "") }

    var existingImages by remember { mutableStateOf(initialProduct?.imageUrls ?: emptyList()) }
    var localImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val isFormValid =
        title.isNotBlank() && description.isNotBlank() && cost.isNotBlank() && (existingImages.isNotEmpty() || localImages.isNotEmpty())

    val isSaving by viewModel.isSaving

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        localImages = localImages + uris
    }

    var tagsString by remember { mutableStateOf(initialProduct?.tags?.joinToString(", ") ?: "") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftBack)
                    .padding(16.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Назад",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.popBackStack() },
                    tint = BlackText
                )
                Text(
                    text = if (initialProduct == null) "Новое объявление" else "Редактирование",
                    fontFamily = Comfortaa,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = BlackText,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                    .background(HardBack)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        val tagsList = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val productData = initialProduct?.copy(
                            title = title,
                            description = description,
                            cost = cost.toLongOrNull() ?: 0L,
                            targetCity = targetCity,
                            category = category,
                            imageUrls = existingImages,
                            tags = tagsList
                        ) ?: Product(
                            title = title,
                            description = description,
                            cost = cost.toLongOrNull() ?: 0L,
                            targetCity = targetCity,
                            category = category,
                            sellerId = currentUid,
                            tags = tagsList
                        )

                        if (initialProduct != null) {
                            viewModel.updateProduct(productData, localImages) {
                                navController.popBackStack()
                            }
                        } else {
                            viewModel.createProduct(productData, localImages) {
                                navController.popBackStack()
                            }
                        }
                    },
                    enabled = isFormValid && !isSaving,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Accent, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "Опубликовать",
                            fontFamily = Comfortaa,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = WhiteText
                        )
                    }
                }
            }
        },
        containerColor = SoftBack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray.copy(alpha = 0.5f))
                                .clickable {
                                    multiplePhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = "Добавить фото",
                                tint = LowAlphaBlackText
                            )
                        }
                    }

                    items(existingImages) { url ->
                        ImagePreviewItem(
                            url = url,
                            onRemove = { existingImages = existingImages - url })
                    }

                    items(localImages) { uri ->
                        ImagePreviewItem(uri = uri, onRemove = { localImages = localImages - uri })
                    }
                }

                ProductTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Название товара"
                )
                ProductTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Описание",
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                )
                ProductTextField(
                    value = cost,
                    onValueChange = { cost = it.filter { char -> char.isDigit() } },
                    label = "Цена (₽)",
                    keyboardType = KeyboardType.Number
                )
                ProductTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = "Категория"
                )
                ProductTextField(
                    value = targetCity,
                    onValueChange = { targetCity = it },
                    label = "Город (опционально)"
                )
                ProductTextField(
                    value = tagsString,
                    onValueChange = { tagsString = it },
                    label = "Теги (через запятую, например: дерево, лампа)"
                )
                // добавить всплывашку или при нажатии окошко с пояснением, что теги помогают персонализировать
                // ленты для пользователей и тем самым продвигать товары с тегами которые вы указали в ленты к вашим потенциальным покупателям

                Spacer(modifier = Modifier.height(30.dp))
            }

            if (isSaving) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Accent)
                }
            }
        }
    }
}


@Composable
fun ImagePreviewItem(url: String? = null, uri: Uri? = null, onRemove: () -> Unit) {
    Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp))) {
        AsyncImage(
            model = url ?: uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().background(Color.LightGray)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Close, contentDescription = "Удалить", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun ProductTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = Comfortaa, color = LowAlphaBlackText) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(fontFamily = Comfortaa, color = BlackText, fontSize = 16.sp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            unfocusedBorderColor = LowAlphaBlackText.copy(alpha = 0.5f),
            cursorColor = Accent
        ),
        modifier = modifier
    )
}
