import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// 1. تعريف الشاشات (بعيداً عن أي import لـ BlendMode)
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Notifications : Screen("notifications")
    object Centers : Screen("centers")
    object Profile : Screen("profile")
}

// 2. الـ Data Class
data class NavigationData(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BloodLinkBottomNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // تعريف العناصر مع التأكد من نداء الـ Icons صح
    val items = listOf(
        NavigationData("Home", Icons.Outlined.Home, Screen.Home.route),
        NavigationData("Notifications", Icons.Outlined.Notifications, Screen.Notifications.route),
        NavigationData("Centers", Icons.Outlined.Map, Screen.Centers.route),
        NavigationData("Profile", Icons.Outlined.Person, Screen.Profile.route)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .shadow(20.dp, RoundedCornerShape(30.dp))
            .background(Color.White, RoundedCornerShape(30.dp))
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                val animatedColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.6f),
                    label = "color"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            if (currentRoute != item.route) { // عشان ما يكررش الشاشة لو هي مفتوحة
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = animatedColor,
                        modifier = Modifier.size(26.dp)
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(5.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                }
            }
        }
    }
}