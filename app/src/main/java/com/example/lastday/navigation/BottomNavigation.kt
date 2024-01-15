package com.example.lastday.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Saved : BottomNavItem("saved", Icons.Default.Bookmark, "Saved")
    object ProfileSettings :
        BottomNavItem("profile_settings", Icons.Default.List, "Profile Settings")
}
