import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.* // تأكد من وجود مكتبة extended icons في build.gradle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.depi_final_project_bloodbank.ui.theme.BloodRed

// 1. تعريف الشاشات (بعيداً عن أي import لـ BlendMode)
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Appeals : Screen("appeals")
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
        NavigationData("Appeals", Icons.Outlined.Bloodtype, Screen.Appeals.route),
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
                    targetValue = if (isSelected) BloodRed else Color.Gray.copy(alpha = 0.6f),
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
                                .background(BloodRed, CircleShape)
                        )
                    }
                }
            }
        }
    }
}