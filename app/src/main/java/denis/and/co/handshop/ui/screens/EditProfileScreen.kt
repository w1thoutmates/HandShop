package denis.and.co.handshop.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import denis.and.co.handshop.ui.theme.Onest


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
                        val newSeller = Seller(
                            id = currentUid,
                            rate = seller?.rate ?: 0.0,
                            reviewsCount = seller?.reviewsCount ?: 0,
                            realName = realName,
                            sellerName = sellerName,
                            description = description,
                            profileImage = seller?.profileImage ?: "",
                            coverImageUrl = seller?.coverImageUrl ?: "",
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
                            workSamples = seller?.workSamples ?: emptyList()
                        )

                        viewModel.saveProfile(newSeller, localAvatarUri, localCoverUri) {

                            if (isFirstCreation) {
                                navController.navigate("main_flow") {
                                    popUpTo("create_profile") { inclusive = true }
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
//                    if (localCoverUri.isNotBlank()) {
//                        AsyncImage(
//                            model = localCoverUri ?: initialSeller?.coverImageUrl,
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    } else {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Icon(Icons.Outlined.Add, contentDescription = null, tint = LowAlphaBlackText)
//                            Text("Изменить обложку", color = LowAlphaBlackText, fontFamily = Comfortaa, fontSize = 12.sp)
//                        }
//                    }

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
//                    if (profileImage.isNotBlank()) {
//                        AsyncImage(
//                            model = localAvatarUri ?: initialSeller?.profileImage,
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    } else {
//                        Icon(Icons.Outlined.Add, contentDescription = null, tint = LowAlphaBlackText)
//                    }

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
                )
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
                )
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
                )
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