package denis.and.co.handshop.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import denis.and.co.handshop.ui.components.AppFooter

@Composable
fun MetricsScreen(
    navController: NavController
) {
    Scaffold(
        bottomBar = { AppFooter(navController) }
    ) { }
}