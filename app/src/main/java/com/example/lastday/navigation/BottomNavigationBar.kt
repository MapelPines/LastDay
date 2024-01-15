package com.example.lastday.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lastday.screens.MainViewModel
import com.example.lastday.screens.create.CreateScreen
import com.example.lastday.screens.home.HomeScreen
import com.example.lastday.screens.profile.ProfileScreen
import com.example.lastday.screens.profile.ProfileSettings
import com.example.lastday.screens.saved.SavedScreen


@Composable
fun NavigationHost(
    viewModel: MainViewModel,
    navController: NavHostController,
) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(viewModel, navigateToSetting = {
                navController.navigate(BottomNavItem.ProfileSettings.route)
            })
        }
        composable(BottomNavItem.Home.route) { HomeScreen(viewModel = viewModel) }
        composable(BottomNavItem.Saved.route) { SavedScreen(viewModel = viewModel) }
        composable(BottomNavItem.ProfileSettings.route) { ProfileSettings(viewModel) }
        composable(MainDirection.CreateScreen.route) {
            CreateScreen(viewModel = viewModel,
                navigateBack = {
                    navController.popBackStack()
                })
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    @Composable
    fun getColor(route: String): Color {
        return if (currentRoute.equals(route))
            MaterialTheme.colorScheme.onTertiary
        else
            MaterialTheme.colorScheme.surface
    }

    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.tertiary, elevation = 8.dp) {

        BottomNavigationItem(
            selected = currentRoute == BottomNavItem.Profile.route,
            onClick = {
                navController.navigate(BottomNavItem.Profile.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    BottomNavItem.Profile.icon,
                    tint = getColor(BottomNavItem.Profile.route),
                    contentDescription = null
                )
            },
            label = {
                Text(
                    BottomNavItem.Profile.label,
                    color = getColor(route = BottomNavItem.Saved.route)
                )
            }
        )

        BottomNavigationItem(
            selected = currentRoute == BottomNavItem.Home.route,
            onClick = {
                navController.navigate(BottomNavItem.Home.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    BottomNavItem.Home.icon,
                    tint = getColor(BottomNavItem.Home.route),
                    contentDescription = null
                )
            },
            label = {
                Text(
                    BottomNavItem.Home.label,
                    color = getColor(route = BottomNavItem.Saved.route)
                )
            }
        )
        BottomNavigationItem(
            selected = currentRoute == BottomNavItem.Saved.route,
            onClick = {
                navController.navigate(BottomNavItem.Saved.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    BottomNavItem.Saved.icon,
                    tint = getColor(BottomNavItem.Saved.route),
                    contentDescription = null
                )
            },
            label = {
                Text(
                    BottomNavItem.Saved.label,
                    color = getColor(route = BottomNavItem.Saved.route)
                )
            }
        )

    }

}