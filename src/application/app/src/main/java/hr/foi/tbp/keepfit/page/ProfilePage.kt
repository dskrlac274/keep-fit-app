package hr.foi.tbp.keepfit.page

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import hr.foi.tbp.keepfit.R
import hr.foi.tbp.keepfit.viewmodel.UserViewModel
import androidx.compose.runtime.LaunchedEffect
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ProfilePage(userViewModel: UserViewModel = viewModel(), onLogout: () -> Unit) {
    val userData by userViewModel.userDataResponse.observeAsState(null)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.tryGetUserData(
            onFailedGetUserData = {
                Toast.makeText(
                    context,
                    userViewModel.errorMessage.value.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    val darkBlue = Color(3, 6, 21)
    val textColor = Color.White

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBlue
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { GoogleBranding(textColor = textColor) }
            item { Spacer(modifier = Modifier.height(20.dp)) }

            userData?.let { user ->
                item {
                    Image(
                        painter = rememberImagePainter(user.image),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFF1565C0), CircleShape)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    LabeledData("Name", user.name, textColor = textColor)
                    LabeledData("Email", user.email, textColor = textColor)
                    LabeledData(
                        "Joined",
                        ZonedDateTime.parse(user.createdAt)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        textColor = textColor
                    )
                }
            } ?: item {
                CircularProgressIndicator(
                    color = Color(0xFF1565C0),
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(50.dp)
                )
            }

            item {
                Icon(
                    painter = painterResource(R.drawable.keep_fit_logo),
                    contentDescription = "Gym Icon",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(200.dp)
                )
            }

            item {
                Text(
                    text = "Health is not about the weight you lose, but about the life you gain.",
                    color = Color(0xFF03A9F4),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            item {
                Button(
                    onClick = { onLogout() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp)
                        .fillMaxWidth(0.6f)
                        .height(50.dp)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun GoogleBranding(textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Data powered by Google",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.padding(end = 8.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.google_logo),
            contentDescription = "Google Logo",
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun LabeledData(label: String, data: String, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label: ",
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = data,
            color = textColor,
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(2f)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}