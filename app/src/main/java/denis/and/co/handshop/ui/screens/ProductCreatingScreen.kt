package denis.and.co.handshop.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import denis.and.co.handshop.data.enums.ProductStatus
import denis.and.co.handshop.data.model.CategoryProvider
import denis.and.co.handshop.data.model.Product
import denis.and.co.handshop.ui.components.CostAnalyticsCard
import denis.and.co.handshop.ui.theme.*
import denis.and.co.handshop.viewmodel.CatalogViewModel
import denis.and.co.handshop.viewmodel.CreateProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreatingScreen(
    navController: NavController,
    viewModel: CreateProductViewModel,
    initialProduct: Product? = null,
    catalogViewModel: CatalogViewModel
) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var title by remember { mutableStateOf(initialProduct?.title ?: "") }
    var titleTouched by remember { mutableStateOf(false) }
    val isTitleError = titleTouched && title.isBlank()
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var cost by remember { mutableStateOf(initialProduct?.cost?.toString() ?: "") }
    var costTouched by remember { mutableStateOf(false) }
    var targetCity by remember { mutableStateOf(initialProduct?.targetCity ?: "") }
    var targetCityTouched by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf(initialProduct?.category ?: "") }

    var existingImages by remember { mutableStateOf(initialProduct?.imageUrls ?: emptyList()) }
    var localImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val isFormValid =
        title.isNotBlank() &&
        category.isNotBlank() &&
        cost.isNotBlank() &&
        targetCity.isNotBlank() &&
        (existingImages.isNotEmpty() || localImages.isNotEmpty())

    val isSaving by viewModel.isSaving

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        localImages = localImages + uris
    }

    var tagsString by remember { mutableStateOf(initialProduct?.tags?.joinToString(", ") ?: "") }

    var selectedStatus by remember { mutableStateOf(initialProduct?.status ?: ProductStatus.ACTIVE) }

    var costAnalytics by remember { mutableStateOf("") }

    val context = LocalContext.current

    var showTagsInfo by remember { mutableStateOf(false) }

    LaunchedEffect(cost, title, category) {
        if (cost.isBlank() || title.isBlank() || category.isBlank()) {
            costAnalytics = ""
            return@LaunchedEffect
        }

        kotlinx.coroutines.delay(500)

        val currentCost = cost.toLongOrNull() ?: 0L

        val tempProduct = Product(
            title = title,
            description = description,
            cost = currentCost,
            category = category,
            sellerId = currentUid,
            tags = tagsString.split(",").map { it.trim() }
        )

        costAnalytics = catalogViewModel.getCostRecommendation(tempProduct)
    }

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
                            tags = tagsList,
                            status = selectedStatus
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
                            Toast.makeText(context, "Объявление [${productData.title}] успешно обновлено", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.createProduct(productData, localImages) {
                                navController.popBackStack()
                            }
                            Toast.makeText(context, "Объявление [${productData.title}] успешно опубликовано", Toast.LENGTH_LONG).show()
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
                    onValueChange = {
                        title = it
                        titleTouched = true
                    },
                    label = "Название товара *",
                    isError = isTitleError
                )
                ProductTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Описание",
                    singleLine = false,
                    modifier = Modifier.height(120.dp).fillMaxWidth()
                )
                ProductTextField(
                    value = cost,
                    onValueChange = {
                        cost = it.filter { char -> char.isDigit() }
                        costTouched = true
                    },
                    label = "Цена (₽) *",
                    isError = costTouched && cost.isBlank(),
                    keyboardType = KeyboardType.Number
                )

                if (cost.isNotBlank() && costAnalytics.isNotBlank()) {
                    CostAnalyticsCard(costAnalytics)
                }

                CategoryDropdown(
                    selectedCategory = category,
                    onCategorySelected = { category = it },
                    categories = CategoryProvider.categories.map { it.name },
                )
                ProductTextField(
                    value = targetCity,
                    onValueChange = {
                        targetCity = it
                        targetCityTouched = true
                    },
                    label = "Город *",
                    isError = targetCityTouched && targetCity.isBlank()
                )
                ProductTextField(
                    value = tagsString,
                    onValueChange = { tagsString = it },
                    label = "Теги (через запятую, например: дерево, мебель, одежда)"
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Зачем нужны теги?",
                        fontFamily = Comfortaa,
                        fontSize = 14.sp,
                        color = BlackText
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Box {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Информация о тегах",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { showTagsInfo = true },
                            tint = LowAlphaBlackText
                        )

                        if (showTagsInfo) {
                            Popup(
                                alignment = Alignment.TopStart,
                                onDismissRequest = { showTagsInfo = false }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 260.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Теги и продвижение",
                                            fontFamily = Onest,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = BlackText
                                        )

                                        HorizontalDivider(
                                            thickness = 1.dp,
                                            color = BlackText.copy(alpha = 0.15f),
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )

                                        Text(
                                            text = "Теги помогают персонализировать ленту для пользователей. " +
                                                    "Товары с корректно указанными тегами чаще показываются " +
                                                    "потенциальным покупателям, которым интересны похожие товары",
                                            fontFamily = Comfortaa,
                                            fontSize = 13.sp,
                                            color = BlackText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (status in ProductStatus.entries) {
                        val isSelected = selectedStatus == status

                        val animatedContainerColor by animateColorAsState(
                            targetValue = if (isSelected) Accent else Accent.copy(alpha = 0.1f),
                            animationSpec = tween(durationMillis = 300),
                            label = "backgroundColor"
                        )

                        val animatedContentColor by animateColorAsState(
                            targetValue = if (isSelected) WhiteText else BlackText,
                            animationSpec = tween(durationMillis = 300),
                            label = "contentColor"
                        )
                        Button(
                            onClick = {
                                selectedStatus = status
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = animatedContainerColor,
                                contentColor = animatedContentColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(
                                text = status.value,
                                fontFamily = Comfortaa,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

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
    modifier: Modifier = Modifier.fillMaxWidth(),
    isError: Boolean = false,
    errorMessage: String = "Это поле обязательно"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = Comfortaa, color = LowAlphaBlackText) },
        isError = isError,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(fontFamily = Comfortaa, color = BlackText, fontSize = 16.sp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            unfocusedBorderColor = LowAlphaBlackText.copy(alpha = 0.5f),
            cursorColor = Accent,
            errorBorderColor = Color.Red
        ),
        modifier = modifier
    )
    if (isError) {
        Text(
            text = errorMessage,
            color = Color.Red,
            fontSize = 12.sp,
            fontFamily = Comfortaa,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            isError = isError,
            label = { Text("Категория", fontFamily = Comfortaa, color = LowAlphaBlackText) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = LowAlphaBlackText.copy(alpha = 0.5f),
                cursorColor = Accent,
                focusedTrailingIconColor = Accent,
                unfocusedTrailingIconColor = LowAlphaBlackText,
                errorBorderColor = Color.Red
            ),
            textStyle = TextStyle(fontFamily = Comfortaa, color = BlackText, fontSize = 16.sp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(WhiteText)
                .exposedDropdownSize()
                .clip(RoundedCornerShape(12.dp))
        ) {
            categories.forEach { categoryName ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = categoryName,
                            fontFamily = Comfortaa,
                            color = BlackText,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onCategorySelected(categoryName)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }
        }

        if (isError) {
            Text(
                text = "Выберите категорию",
                color = Color.Red,
                fontSize = 12.sp,
                fontFamily = Comfortaa,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
