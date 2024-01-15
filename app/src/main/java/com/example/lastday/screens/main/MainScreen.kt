package com.example.lastday.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.lastday.navigation.BottomNavigationBar
import com.example.lastday.navigation.NavigationHost
import com.example.lastday.screens.MainViewModel
import com.example.lastday.navigation.BottomNavItem
import com.example.lastday.navigation.MainDirection

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit,
    onSignOut: () -> Unit,
) {
    val navController = rememberNavController()
    var canPop by remember { mutableStateOf(false) }
    var showFab by remember { mutableStateOf(false) }
    LaunchedEffect(navController.currentBackStackEntryFlow) {
        navController.currentBackStackEntryFlow.collect {
            showFab = when (it.destination.route) {
                BottomNavItem.Home.route -> true
                else -> false
            }

            canPop = when (it.destination.route) {
                BottomNavItem.ProfileSettings.route -> true
                MainDirection.CreateScreen.route -> true
                else -> false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.tertiary),
                title = {},
                navigationIcon = {
                    Row {
                        if (canPop) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        IconButton(onClick = onThemeChange) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                contentDescription = "",
                                tint = if (isDarkTheme) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.tertiaryContainer
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(MainDirection.CreateScreen.route)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    })
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = {
            Box(
                Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                NavigationHost(viewModel, navController)
            }
        })

}