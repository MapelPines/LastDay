package com.example.lastday.navigation


sealed class MainDirection(
    protected val appScreen: AppScreen
) {

    object Splash : MainDirection(
        appScreen = AppScreen.SPLASH
    ) {
        val route = appScreen.path
    }

    object SignIn : MainDirection(
        appScreen = AppScreen.SIGN_IN
    ) {
        val route = appScreen.path
    }

    object SignUp : MainDirection(
        appScreen = AppScreen.SIGN_UP
    ) {
        val route = appScreen.path
    }

    object MainScreen : MainDirection(
        appScreen = AppScreen.MAIN
    ) {
        val route = appScreen.path
    }

    object CreateScreen : MainDirection(
        appScreen = AppScreen.CREATE
    ) {
        val route = appScreen.path
    }
}


enum class AppScreen(
    val path: String,
) {

    SPLASH("splash"),
    SIGN_IN("sign_in"),
    SIGN_UP("sign_up"),
    MAIN("main_screen"),
    CREATE("create"),
}