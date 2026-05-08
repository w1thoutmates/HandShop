package denis.and.co.handshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import denis.and.co.handshop.data.model.MetricsProvider
import denis.and.co.handshop.data.model.Seller
import denis.and.co.handshop.ui.components.AppFooter
import denis.and.co.handshop.ui.components.ExpandableList
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Onest
import denis.and.co.handshop.ui.theme.SoftBack
import denis.and.co.handshop.viewmodel.ProfileViewModel

@Composable
fun MetricsScreen(
    navController: NavController,
    sellerId: String,
    viewModel: ProfileViewModel
) {
    val seller by viewModel.seller.collectAsState()

    LaunchedEffect(sellerId) {
        viewModel.loadProfile(sellerId)
    }

    seller?.let { currentSeller ->
        Scaffold(
            containerColor = Color(currentSeller.selfProfileBackground.toColorInt()),
            bottomBar = { AppFooter(navController, currentSeller) },
            modifier = Modifier
                .fillMaxSize()
        ) { padding ->
            MetricsScreenContent(
                navController = navController,
                modifier = PaddingValues(0.dp),
                sellerId = sellerId,
                seller = currentSeller,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun MetricsScreenContent(
    navController: NavController,
    modifier: PaddingValues,
    sellerId: String,
    seller: Seller,
    viewModel: ProfileViewModel
) {
    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .padding(modifier)
                .fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 25.dp).fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .size(40.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )

                Text(
                    text = "Графики со сводкой статистик",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = Onest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(seller.selfProfileTextColor.toColorInt())
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            ExpandableList(
                items = MetricsProvider.getItems(sellerId),
                onChildClick = { child ->
                    navController.navigate(child.route)
                },
                seller = seller,
                viewModel = viewModel
            )

        }
    }
}