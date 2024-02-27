package hr.foi.tbp.keepfit.page

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem("home", Icons.Outlined.Home, "Home"),
        NavigationItem("diary", Icons.Outlined.Create, "Diary"),
        NavigationItem("health", Icons.Outlined.Refresh, "Health"),
        NavigationItem("profile", Icons.Outlined.AccountCircle, "Profile")
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var selectedItem by remember { mutableStateOf(items.first()) }

    BottomNavigation(
        backgroundColor = Color(1, 4, 33),
        contentColor = Color.White
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, color = Color.White) },
                selected = currentRoute == item.route,
                selectedContentColor = Color.Cyan,
                unselectedContentColor = Color.White.copy(0.4f),
                onClick = {
                    selectedItem = item
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val title: String
)