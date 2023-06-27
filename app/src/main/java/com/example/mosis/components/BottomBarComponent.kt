import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mosis.data.NavigationItem
import com.example.mosis.navigation.MosisAppRouter
import com.example.mosis.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomBarComponent(currentRoute: Screen) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Scaffold(
            bottomBar = {
                BottomBar(
                    listOf(
                        NavigationItem.Home,
                        NavigationItem.Done,
                        NavigationItem.Created,
                        NavigationItem.Map
                    ),
                    currentRoute,
                    onNavigationItemSelected = { route ->
                        MosisAppRouter.navigateTo(route)
                    }
                )
            }
        ) {}
    }
}

@Composable
fun BottomBar(
    navigationItems: List<NavigationItem>,
    currentRoute: Screen,
    onNavigationItemSelected: (Screen) -> Unit
) {
    NavigationBar()
    {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavigationItemSelected(item.route) })
        }
    }
}