package hr.foi.tbp.keepfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.foi.tbp.keepfit.auth.Auth
import hr.foi.tbp.keepfit.auth.SharedPreferenceUserManager
import hr.foi.tbp.keepfit.helper.GoogleAuthHelper
import hr.foi.tbp.keepfit.helper.JwtHelper.Companion.isTokenValid
import hr.foi.tbp.keepfit.page.BottomNavigationBar
import hr.foi.tbp.keepfit.page.DiaryPage
import hr.foi.tbp.keepfit.page.EntryPage
import hr.foi.tbp.keepfit.page.GoalPage
import hr.foi.tbp.keepfit.page.HealthPage
import hr.foi.tbp.keepfit.page.HomePage
import hr.foi.tbp.keepfit.page.ProfilePage
import hr.foi.tbp.keepfit.ui.theme.KeepFitTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KeepFitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color(0xFFFFFFFF)
                ) {
                    val navController = rememberNavController()
                    val authUser = SharedPreferenceUserManager(this).getAuthUserData()
                    Auth.authUserData = authUser

                    var isAuthenticated by remember { mutableStateOf(isTokenValid(authUser)) }

                    val currentRoute =
                        navController.currentBackStackEntryAsState().value?.destination?.route
                    val mainRoutes = listOf("entry", "home", "health", "diary", "profile")
                    val canPop = if (currentRoute != null) {
                        currentRoute !in mainRoutes
                    } else {
                        false
                    }

                    Scaffold(
                        topBar = {
                            if (currentRoute != "entry" && currentRoute != null)
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = "KeepFit",
                                            color = Color.White,
                                        )
                                    },
                                    navigationIcon = if (canPop) {
                                        {
                                            IconButton(onClick = { navController.popBackStack() }) {
                                                Icon(
                                                    imageVector = Icons.Filled.ArrowBack,
                                                    contentDescription = "Back Arrow",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    } else {
                                        {}
                                    },
                                    actions = {
                                        if (currentRoute != "goal") {
                                            IconButton(onClick = {
                                                navController.navigate("goal")
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Settings,
                                                    contentDescription = "Top Bar Action",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    },
                                    colors = topAppBarColors(
                                        containerColor = Color(1, 4, 33)
                                    )
                                )
                        },
                        bottomBar = {
                            if (isAuthenticated) {
                                BottomNavigationBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController,
                            startDestination = if (isAuthenticated) "home" else "entry",
                            Modifier.padding(innerPadding)
                        ) {
                            composable("entry") {
                                EntryPage(
                                    onSuccessfulLogin = {
                                        isAuthenticated = true
                                        navController.navigate("home") {
                                            popUpTo("entry") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("home") { HomePage() }
                            composable("diary") { DiaryPage() }
                            composable("health") { HealthPage() }
                            composable("profile") {
                                ProfilePage(onLogout = {
                                    isAuthenticated = false
                                    Auth.authUserData = null
                                    SharedPreferenceUserManager(this@MainActivity).clearUserData()
                                    GoogleAuthHelper.getGoogleSignInClient(this@MainActivity)
                                        .signOut()
                                    navController.navigate("entry") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                })
                            }
                            composable("goal") { GoalPage() }
                        }
                    }
                }
            }
        }
    }
}