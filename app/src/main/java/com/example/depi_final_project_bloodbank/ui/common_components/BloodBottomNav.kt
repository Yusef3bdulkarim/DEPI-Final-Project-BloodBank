import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.ui.theme.BloodRed
import androidx.compose.material.icons.outlined.* // بنستخدم الـ Outlined عشان تبان احترافية ورقيقة
@Composable
fun BloodLinkBottomNav() {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        NavigationData("Home", Icons.Outlined.Home),
        NavigationData("Appeals", Icons.Outlined.Bloodtype), // أيقونة نقطة دم رسمية
        NavigationData("Centers", Icons.Outlined.Map),
        NavigationData("Profile", Icons.Outlined.Person)
    )

    // ده البار "العايم"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp) // مسافة من الجوانب ومن تحت
            .shadow(20.dp, RoundedCornerShape(30.dp)) // ظل قوي واحترافي
            .background(Color.White, RoundedCornerShape(30.dp)) // خلفية بيضاء دائرية تماماً
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItem == index

                // أنيميشن لتغيير اللون
                val animatedColor by animateColorAsState(
                    targetValue = if (isSelected) BloodRed else Color.Gray.copy(alpha = 0.6f),
                    label = "color"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedItem = index }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = animatedColor,
                        modifier = Modifier.size(26.dp)
                    )

                    if (isSelected) {
                        // نقطة صغيرة تحت الأيقونة المختارة بدل النص عشان يبقى "Minimalist"
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

data class NavigationData(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)