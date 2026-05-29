package denis.and.co.handshop.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import coil.compose.AsyncImage
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.ui.components.ColorPickerDialog
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.viewmodel.EditProfileViewModel
import androidx.core.graphics.ColorUtils
import denis.and.co.handshop.data.model.WorkSample
import denis.and.co.handshop.ui.components.AddWorkSampleDialog
import denis.and.co.handshop.ui.components.WorkSampleCard
import denis.and.co.handshop.ui.components.WorkSampleDetailsDialog
import denis.and.co.handshop.ui.navigation.CreateProfileRoute
import denis.and.co.handshop.ui.navigation.RecommendationRoute
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.utils.toHexString


@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel,
    isFirstCreation: Boolean = false
) {
    val seller = viewModel.sellerState

    if (!isFirstCreation && seller == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Accent)
        }
        return
    }

    var realName by remember { mutableStateOf(seller?.realName ?: "") }
    var sellerName by remember { mutableStateOf(seller?.sellerName ?: "") }
    var description by remember { mutableStateOf(seller?.description ?: "") }

    var phone by remember { mutableStateOf(seller?.contacts?.get("Номер телефона") ?: "") }
    var email by remember { mutableStateOf(seller?.contacts?.get("Почта") ?: "") }
    var telegram by remember { mutableStateOf(seller?.contacts?.get("Телеграм") ?: "") }

    var localCoverUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var localAvatarUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> localCoverUri = uri }
    )

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> localAvatarUri = uri }
    )

    var workSamples by remember { mutableStateOf(seller?.workSamples ?: emptyList()) }
    var showAddWorkSampleDialog by remember { mutableStateOf(false) }

    var editingWorkSample by remember { mutableStateOf<WorkSample?>(null) }
    var detailsWorkSample by remember { mutableStateOf<WorkSample?>(null) }

    LaunchedEffect(seller) {
        viewModel.initColorsFromSeller(seller)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftBack)
                    .padding(16.dp)
                    .windowInsetsPadding(WindowInsets.statusBars),
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
                    text = if (seller == null) "Создание профиля" else "Редактирование",
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
                        val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        val baseSeller = seller ?: Seller(id = currentUid)
                        val newSeller = baseSeller.copy(
                            id = currentUid,
                            realName = realName,
                            sellerName = sellerName,
                            description = description,
                            selfProfileBackground = viewModel.selfProfileBackground,
                            selfProfileTextColor = viewModel.selfProfileTextColor,
                            selfProfileFooterColor = viewModel.selfProfileFooterColor,
                            selfProfileAccentColor = viewModel.selfProfileAccentColor,
                            selfProfileAccentTextColor = viewModel.selfProfileAccentTextColor,
                            selfProfileIconsColor = viewModel.selfProfileIconsColor,
                            contacts = mapOf(
                                "Номер телефона" to phone,
                                "Почта" to email,
                                "Телеграм" to telegram
                            ).filterValues { it.isNotBlank() },
                            workSamples = workSamples
                        )

                        viewModel.saveProfile(newSeller, localAvatarUri, localCoverUri) {

                            if (isFirstCreation) {
                                navController.navigate(RecommendationRoute) {
                                    popUpTo(CreateProfileRoute) { inclusive = true }
                                }
                            } else {
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Сохранить",
                        fontFamily = Comfortaa,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = WhiteText
                    )
                }
            }
        },
        modifier = Modifier
            .background(SoftBack)
            .fillMaxSize(),
        containerColor = SoftBack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SoftBack)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                        .clickable {
                            coverPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                       },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = localCoverUri ?: seller?.coverImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            avatarPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {

                    AsyncImage(
                        model = localAvatarUri ?: seller?.profileImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            ProfileTextField(value = sellerName, onValueChange = { sellerName = it }, label = "Имя продавца / Псевдоним")
            ProfileTextField(value = realName, onValueChange = { realName = it }, label = "Настоящее имя")

            ProfileTextField(
                value = description,
                onValueChange = { description = it },
                label = "О себе / Описание магазина",
                singleLine = false,
                modifier = Modifier.height(120.dp).fillMaxWidth()
            )

            Text(
                text = "Контакты",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlackText,
                fontFamily = Comfortaa,
                modifier = Modifier.padding(top = 8.dp)
            )

            ProfileTextField(value = phone, onValueChange = { phone = it }, label = "Номер телефона")
            ProfileTextField(value = email, onValueChange = { email = it }, label = "Электронная почта")
            ProfileTextField(value = telegram, onValueChange = { telegram = it }, label = "Телеграм (@username)")

            Text(
                text = "Портфолио",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlackText,
                fontFamily = Comfortaa
            )

            if (workSamples.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HardBack)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Добавьте примеры своих работ,\nчтобы привлечь больше покупателей",
                        fontFamily = Comfortaa,
                        fontSize = 14.sp,
                        color = BlackText.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(workSamples, key = { it.id }) { sample ->
                    Box(modifier = Modifier.width(240.dp)) {
                        WorkSampleCard(
                            workSample = sample,
                            onClick = { detailsWorkSample = sample }
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.55f))
                                    .clickable { editingWorkSample = sample },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Редактировать",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935).copy(alpha = 0.75f))
                                    .clickable { workSamples = workSamples - sample },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Удалить",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .size(120.dp, 160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Accent.copy(alpha = 0.12f))
                            .border(
                                width = 1.5.dp,
                                color = Accent.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { showAddWorkSampleDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                tint = Accent,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Добавить",
                                fontFamily = Comfortaa,
                                fontWeight = FontWeight.Bold,
                                color = Accent,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            if (showAddWorkSampleDialog) {
                AddWorkSampleDialog(
                    onDismiss = { showAddWorkSampleDialog = false },
                    onAdd = { newSample ->
                        workSamples = workSamples + newSample
                    },
                    imageRepo = viewModel.imageRepository
                )
            }

            editingWorkSample?.let { sampleToEdit ->
                AddWorkSampleDialog(
                    existingSample = sampleToEdit,
                    onDismiss = { editingWorkSample = null },
                    onAdd = { updatedSample ->
                        workSamples = workSamples.map {
                            if (it.id == updatedSample.id) updatedSample else it
                        }
                        editingWorkSample = null
                    },
                    imageRepo = viewModel.imageRepository
                )
            }

            seller?.let { currentSeller ->
                detailsWorkSample?.let { sample ->
                    WorkSampleDetailsDialog(
                        workSample = sample,
                        onDismiss = { detailsWorkSample = null },
                        seller = currentSeller
                    )
                }
            }

            Text(
                text = "Цветовая палитра профиля",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = BlackText,
                fontFamily = Comfortaa,
                modifier = Modifier.padding(top = 8.dp)
            )

            ColorField(
                color = Color(viewModel.selfProfileBackground.toColorInt()),
                onColorChange = { viewModel.setSelfProfileBackgroundColor(it) },
                label = "Основной цвет (задний фон)"
            )

            ColorField(
                color = Color(viewModel.selfProfileTextColor.toColorInt()),
                onColorChange = { viewModel.setSelfProfileTextColor(it) },
                label = "Основной цвет текста"
            )

            if (!isContrasted(
                    Color(viewModel.selfProfileTextColor.toColorInt()),
                    Color(viewModel.selfProfileBackground.toColorInt())
                ) &&
                !(viewModel.selfProfileTextColor == BlackText.toHexString() &&
                viewModel.selfProfileBackground == SoftBack.toHexString())
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .background(SoftBack)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.Black.copy(0.1f))
                ) {
                    Text(
                        text = "⚠ Внимание: Текст будет плохо читаться на выбранном фоне. Выберите более контрастные цвета.",
                        style = TextStyle(
                            fontFamily = Onest,
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            ColorField(
                color = Color(viewModel.selfProfileAccentColor.toColorInt()),
                onColorChange = { viewModel.setSelfProfileAccentColor(it) },
                label = "Акцентированный цвет (цвет кнопок)"
            )

            ColorField(
                color = Color(viewModel.selfProfileAccentTextColor.toColorInt()),
                onColorChange = { viewModel.setSelfProfileAccentTextColor(it) },
                label = "Акцентированный цвет текста (цвет текста на кнопках)"
            )

            if (!isContrasted(
                    Color(viewModel.selfProfileAccentTextColor.toColorInt()),
                    Color(viewModel.selfProfileAccentColor.toColorInt())
                ) &&
                !(viewModel.selfProfileAccentColor == Accent.toHexString() &&
                viewModel.selfProfileAccentTextColor == WhiteText.toHexString())
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .background(SoftBack)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.Black.copy(0.1f))
                ) {
                    Text(
                        text = "⚠ Внимание: Текст будет плохо читаться на выбранном фоне. Выберите более контрастные цвета.",
                        style = TextStyle(
                            fontFamily = Onest,
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            ColorField(
                color = Color(viewModel.selfProfileFooterColor.toColorInt()),
                onColorChange = { viewModel.setSelfProfileFooterColor(it) },
                label = "Цвет панели навигации"
            )

            ColorField(
                color = Color(viewModel.selfProfileIconsColor.toColorInt()),
                onColorChange = { viewModel.setSelfProfileIconsColor(it) },
                label = "Цвет иконок"
            )

            if (!isContrasted(
                    Color(viewModel.selfProfileFooterColor.toColorInt()),
                    Color(viewModel.selfProfileIconsColor.toColorInt())
                ) &&
                !(viewModel.selfProfileFooterColor == HardBack.toHexString() &&
                viewModel.selfProfileIconsColor == BlackText.toHexString())
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .background(SoftBack)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.Black.copy(0.1f))
                ) {
                    Text(
                        text = "⚠ Внимание: Иконки будут плохо видны на выбранном фоне. Выберите более контрастные цвета.",
                        style = TextStyle(
                            fontFamily = Onest,
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = Comfortaa, color = LowAlphaBlackText) },
        singleLine = singleLine,
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

@Composable
fun ColorField(
    color: Color,
    onColorChange: (Color) -> Unit,
    label: String
) {
    var showPicker by remember { mutableStateOf(false) }

    val hex = remember(color) {
        "#%02X%02X%02X".format(
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = { showPicker = true }
            )
    ) {
        OutlinedTextField(
            value = hex,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(label, fontFamily = Comfortaa, color = LowAlphaBlackText)
            },
            textStyle = TextStyle(
                fontFamily = Comfortaa,
                color = BlackText,
                fontSize = 16.sp
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = LowAlphaBlackText.copy(alpha = 0.5f),
                cursorColor = Accent
            ),
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = false
        )
    }

    if (showPicker) {
        ColorPickerDialog(
            initialColor = color,
            onDismiss = { showPicker = false },
            onColorSelected = {
                onColorChange(it)
                showPicker = false
            }
        )
    }
}

fun isContrasted(foreground: Color, background: Color): Boolean {
    val fgInt = android.graphics.Color.rgb(
        (foreground.red * 255).toInt(),
        (foreground.green * 255).toInt(),
        (foreground.blue * 255).toInt()
    )
    val bgInt = android.graphics.Color.rgb(
        (background.red * 255).toInt(),
        (background.green * 255).toInt(),
        (background.blue * 255).toInt()
    )

    return ColorUtils.calculateContrast(fgInt, bgInt) >= 4.5
}