package com.example.lastday.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.example.lastday.screens.MainViewModel
import com.example.lastday.screens.authentication.LoginScreen
import com.example.lastday.screens.authentication.RegisterScreen
import com.example.lastday.screens.main.MainScreen
import com.example.lastday.screens.splash.SplashScreen


@Composable
fun MainNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    startDestination: String = MainDirection.Splash.route,
    onThemeChange: () -> Unit,
    isDarkTheme: Boolean,
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        splashScreenNav(navController)
        signInScreenNav(viewModel, navController)
        signUpScreenNav(viewModel, navController)
        mainScreenNav(viewModel, isDarkTheme, navController, onThemeChange)

    }
}

private fun NavGraphBuilder.splashScreenNav(
    navController: NavHostController,
) {
    composable(
        route = MainDirection.Splash.route
    ) {
        SplashScreen(
            navigateToHome = {
                navController.navigate(MainDirection.MainScreen.route, navOptions {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                })
            },
            navigateToSignIn = {
                navController.navigate(MainDirection.SignIn.route, navOptions {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                })
            }
        )
    }
}

private fun NavGraphBuilder.signInScreenNav(
    viewModel: MainViewModel,
    navController: NavHostController,
) {
    composable(
        route = MainDirection.SignIn.route
    ) {
        LoginScreen(
            viewModel = viewModel,
            navigateToHome = {
                navController.navigate(MainDirection.MainScreen.route, navOptions {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                })
            },
            navigateToRegister = { navController.navigate(MainDirection.SignUp.route) },
        )
    }
}


private fun NavGraphBuilder.signUpScreenNav(
    viewModel: MainViewModel,
    navController: NavHostController,
) {
    composable(
        route = MainDirection.SignUp.route
    ) {
        RegisterScreen(
            viewModel = viewModel,
            navigateToHomeScreen = {
                navController.navigate(MainDirection.MainScreen.route, navOptions {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                })
            }
        )
    }
}

private fun NavGraphBuilder.mainScreenNav(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    navController: NavHostController,
    onThemeChange: () -> Unit
) {
    composable(
        route = MainDirection.MainScreen.route
    ) {
        MainScreen(
            viewModel = viewModel,
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange,
            onSignOut = {
                viewModel.signOut()
                navController.navigate(MainDirection.SignIn.route, navOptions {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                })
            }
        )
    }
}

fun NavGraphBuilder.createScreenNav(
    viewModel: MainViewModel,
    navController: NavHostController,
) {

}