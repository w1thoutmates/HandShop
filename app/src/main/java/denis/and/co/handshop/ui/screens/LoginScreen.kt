package denis.and.co.handshop.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import denis.and.co.handshop.R
import denis.and.co.handshop.ui.theme.Accent
import denis.and.co.handshop.ui.theme.BlackText
import denis.and.co.handshop.ui.theme.Comfortaa
import denis.and.co.handshop.ui.theme.LowAlphaBlackText
import denis.and.co.handshop.ui.theme.SoftBack

@Composable
fun LoginScreen(onAuthSuccess: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val signInClient = Identity.getSignInClient(context)
    val clientId = stringResource(R.string.default_web_client_id)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnSuccessListener {
                            onAuthSuccess()
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                        }
                }
            } catch (ex: Exception) { ex.printStackTrace() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBack)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = "Доска объявлений уникальных\nвещей ручной работы",
                style = TextStyle(
                    fontFamily = Comfortaa,
                    color = LowAlphaBlackText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 125.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Авторизуйтесь, чтобы продолжить",
            fontFamily = Comfortaa,
            fontSize = 14.sp,
            color = BlackText.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                signInClient.signOut().addOnCompleteListener {
                    val request = GetSignInIntentRequest.builder()
                        .setServerClientId(clientId)
                        .build()

                    signInClient.getSignInIntent(request)
                        .addOnSuccessListener { result ->
                            launcher.launch(IntentSenderRequest.Builder(result.intentSender).build())
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(R.drawable.google_image),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp).size(25.dp),
                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.55f))
                )

                Text(
                    "Войти через Google",
                    fontFamily = Comfortaa,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryLoginButton(text = "По номеру телефона", Icons.Outlined.Phone)
        Spacer(modifier = Modifier.height(8.dp))
        SecondaryLoginButton(text = "Через почту", Icons.Outlined.Email)

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SecondaryLoginButton(text: String, icon: ImageVector) {
    Button(
        onClick = {},
        enabled = false,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, LowAlphaBlackText.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 10.dp)
        )
        Text(
            text = text,
            fontFamily = Comfortaa,
            color = LowAlphaBlackText.copy(alpha = 0.4f),
            fontSize = 14.sp
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBack)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = "Доска объявлений уникальных\nвещей ручной работы",
                style = TextStyle(
                    fontFamily = Comfortaa,
                    color = LowAlphaBlackText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 125.dp)
            )
        }



        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Авторизуйтесь, чтобы продолжить",
            fontFamily = Comfortaa,
            fontSize = 14.sp,
            color = BlackText.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {},
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(R.drawable.google_image),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 10.dp).size(25.dp),
                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.55f))
                )

                Text(
                    "Войти через Google",
                    fontFamily = Comfortaa,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryLoginButton(text = "По номеру телефона", Icons.Outlined.Phone)
        Spacer(modifier = Modifier.height(8.dp))
        SecondaryLoginButton(text = "Через почту", Icons.Outlined.Email)

        Spacer(modifier = Modifier.height(40.dp))
    }
}