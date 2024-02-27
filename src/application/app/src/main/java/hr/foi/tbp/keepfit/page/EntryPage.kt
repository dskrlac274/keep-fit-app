package hr.foi.tbp.keepfit.page

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.tbp.keepfit.R
import hr.foi.tbp.keepfit.auth.SharedPreferenceUserManager
import hr.foi.tbp.keepfit.contract.GoogleAuthContract
import hr.foi.tbp.keepfit.helper.GoogleAuthHelper
import hr.foi.tbp.keepfit.viewmodel.AuthViewModel

@Composable
fun EntryPage(
    onSuccessfulLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val googleAuthResultLauncher =
        rememberLauncherForActivityResult(
            GoogleAuthContract(
                GoogleAuthHelper.getGoogleSignInClient(
                    context
                )
            )
        ) {
            val idToken = it?.result?.idToken

            if (idToken != null) {

                authViewModel.tryAuthenticate(
                    idToken,
                    onSuccessfulLogin = {
                        SharedPreferenceUserManager(context).saveAuthUserData()
                        onSuccessfulLogin()
                    },
                    onFailedLogin = {
                        Toast.makeText(
                            context,
                            authViewModel.errorMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            } else {
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.keep_fit_background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.keep_fit_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    Text(
                        text = "Keep Fit",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    googleAuthResultLauncher.launch(1)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF5F5F5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Login with Google", color = Color(111, 115, 117))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                .padding(8.dp)
        ) {
            Text(
                text = "© 2024 Daniel Škrlac",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}