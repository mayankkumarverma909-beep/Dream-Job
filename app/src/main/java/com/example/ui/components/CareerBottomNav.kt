package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.NavDestination

@Composable
fun CareerBottomNav(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomDestinations = listOf(
        NavDestination.DASHBOARD,
        NavDestination.JOBS,
        NavDestination.INTERVIEW,
        NavDestination.RESUME,
        NavDestination.APPLICATIONS
    )

    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .testTag("career_bottom_navigation")
    ) {
        bottomDestinations.forEach { dest ->
            val isSelected = currentDestination == dest
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(dest) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = dest.title
                    )
                },
                label = {
                    Text(
                        text = dest.title,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = true,
                modifier = Modifier.testTag("nav_item_${dest.route}")
            )
        }
    }
}
