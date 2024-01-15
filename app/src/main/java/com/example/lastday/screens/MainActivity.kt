package com.example.lastday.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.lastday.components.LoadingDialog
import com.example.lastday.ui.theme.LastDayTheme
import com.example.lastday.navigation.MainNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        setContent {
            val navController = rememberNavController()
            var isDarkTheme by remember { mutableStateOf(false) }
            val context = LocalContext.current

            LastDayTheme(isDarkTheme) {
                MainNavGraph(
                    viewModel = viewModel,
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = {
                        isDarkTheme = !isDarkTheme
                    }
                )

                if (viewModel.showLoadingDialog) {
                    LoadingDialog()
                }

                viewModel.statusMessage?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    viewModel.statusMessage = null
                }
            }
        }
    }
}