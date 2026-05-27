package denis.and.co.handshop.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import denis.and.co.handshop.data.model.Complaint
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.HardBack
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.ui.theme.WhiteText
import denis.and.co.handshop.utils.toRelativeDateString
import denis.and.co.handshop.viewmodel.ModeratorUiState
import denis.and.co.handshop.viewmodel.ModeratorViewModel

@Composable
fun ModeratorScreen(
    navController: NavController,
    viewModel: ModeratorViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()
    val warningsMap by viewModel.targetSellerWarnings.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var showClearConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SoftBack,
        bottomBar = { AppFooter(navController) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(HardBack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HardBack)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HardBack)
                        .padding(vertical = 16.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .clickable { navController.popBackStack() },
                        tint = BlackText
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Модерация",
                            fontFamily = Onest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = BlackText
                        )
                        val count = (uiState as? ModeratorUiState.Success)?.complaints?.size ?: 0
                        if (count > 0) {
                            Text(
                                text = "$count жалоб ожидает рассмотрения",
                                fontFamily = Comfortaa,
                                fontSize = 13.sp,
                                color = LowAlphaBlackText
                            )
                        }
                    }

                    TextButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Очистить",
                            tint = Accent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Очистить",
                            fontFamily = Comfortaa,
                            fontSize = 13.sp,
                            color = Accent
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftBack)
            ) {
                when (val state = uiState) {
                    is ModeratorUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Accent)
                        }
                    }

                    is ModeratorUiState.Empty -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "✓",
                                    fontSize = 48.sp,
                                    color = Color(0xFF65BD5E)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Жалоб нет",
                                    fontFamily = Onest,
                                    fontSize = 20.sp,
                                    color = LowAlphaBlackText,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Все жалобы обработаны",
                                    fontFamily = Comfortaa,
                                    fontSize = 14.sp,
                                    color = LowAlphaBlackText.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    is ModeratorUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.message, color = Color.Red, fontFamily = Comfortaa)
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.loadComplaints() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                                ) {
                                    Text("Повторить", color = WhiteText)
                                }
                            }
                        }
                    }

                    is ModeratorUiState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.complaints, key = { it.id }) { complaint ->
                                ComplaintCard(
                                    complaint = complaint,
                                    warningsCount = warningsMap[complaint.targetSellerId] ?: 0,
                                    onWarn = { viewModel.warnSeller(complaint.targetSellerId, complaint.id) },
                                    onBan = { viewModel.banSeller(complaint.targetSellerId) },
                                    onReject = { viewModel.rejectComplaint(complaint.id) },
                                    onIgnoreReporter = { viewModel.ignoreReporter(complaint.reporterSellerId, complaint.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = WhiteText,
            title = {
                Text(
                    "Очистить обработанные жалобы?",
                    fontFamily = Onest,
                    fontWeight = FontWeight.Bold,
                    color = BlackText
                )
            },
            text = {
                Text(
                    "Все жалобы со статусом «Решено», «Отклонено» и «Проигнорировано» будут удалены безвозвратно.",
                    fontFamily = Comfortaa,
                    fontSize = 14.sp,
                    color = LowAlphaBlackText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllResolved()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("Очистить", color = WhiteText, fontFamily = Comfortaa)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Отмена", fontFamily = Comfortaa, color = LowAlphaBlackText)
                }
            }
        )
    }
}

@Composable
private fun ComplaintCard(
    complaint: Complaint,
    warningsCount: Int,
    onWarn: () -> Unit,
    onBan: () -> Unit,
    onReject: () -> Unit,
    onIgnoreReporter: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showBanConfirm by remember { mutableStateOf(false) }
    val canBan = warningsCount >= 3

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteText),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = complaint.reporterAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                    Text(
                        text = complaint.reporterName,
                        fontFamily = Onest,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = BlackText
                    )
                    Text(
                        text = "подал жалобу",
                        fontFamily = Comfortaa,
                        fontSize = 11.sp,
                        color = LowAlphaBlackText
                    )
                }
                Text(
                    text = complaint.date.toRelativeDateString(),
                    fontFamily = Comfortaa,
                    fontSize = 11.sp,
                    color = LowAlphaBlackText
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SoftBack)
                    .padding(12.dp)
            ) {
                Text(
                    text = complaint.text,
                    fontFamily = Comfortaa,
                    fontSize = 13.sp,
                    color = BlackText,
                    lineHeight = 18.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFFFB3A7), RoundedCornerShape(10.dp))
                    .background(Color(0x0FFF3B30))
                    .padding(10.dp)
            ) {
                AsyncImage(
                    model = complaint.targetSellerAvatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                    Text(
                        text = "Жалоба на:",
                        fontFamily = Comfortaa,
                        fontSize = 11.sp,
                        color = Color(0xFFE53935)
                    )
                    Text(
                        text = complaint.targetSellerName,
                        fontFamily = Onest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BlackText
                    )
                }

                if (warningsCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (canBan) Color(0xFFE53935)
                                else Color(0xFFFF9800)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⚠ $warningsCount/3",
                            fontFamily = Onest,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = WhiteText
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (expanded) "Скрыть действия ▲" else "Действия ▼",
                    fontFamily = Comfortaa,
                    fontSize = 13.sp,
                    color = Accent
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(color = LowAlphaBlackText.copy(0.1f))

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = onWarn,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = WhiteText
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Выдать предупреждение",
                            fontFamily = Comfortaa,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = WhiteText
                        )
                    }

                    if (canBan) {
                        Button(
                            onClick = { showBanConfirm = true },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Block,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = WhiteText
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Заблокировать аккаунт",
                                fontFamily = Comfortaa,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = WhiteText
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.ThumbDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = LowAlphaBlackText
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Отклонить",
                                fontFamily = Comfortaa,
                                fontSize = 13.sp,
                                color = LowAlphaBlackText
                            )
                        }

                        OutlinedButton(
                            onClick = onIgnoreReporter,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.PersonOff,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = LowAlphaBlackText
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Игнор",
                                fontFamily = Comfortaa,
                                fontSize = 13.sp,
                                color = LowAlphaBlackText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBanConfirm) {
        AlertDialog(
            onDismissRequest = { showBanConfirm = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = WhiteText,
            title = {
                Text(
                    "Заблокировать «${complaint.targetSellerName}»?",
                    fontFamily = Onest,
                    fontWeight = FontWeight.Bold,
                    color = BlackText,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    "Все товары продавца будут удалены. В профиле появится метка «Заблокирован». Действие нельзя отменить.",
                    fontFamily = Comfortaa,
                    fontSize = 14.sp,
                    color = LowAlphaBlackText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBan()
                        showBanConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Заблокировать", color = WhiteText, fontFamily = Comfortaa, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanConfirm = false }) {
                    Text("Отмена", fontFamily = Comfortaa, color = LowAlphaBlackText)
                }
            }
        )
    }
}