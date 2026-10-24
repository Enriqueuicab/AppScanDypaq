package com.example.ultimamilla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.ultimamilla.propuesta.Screen.LoginScreen
import com.example.ultimamilla.propuesta.Screen.UltimaMillaApp
import com.example.ultimamilla.propuesta.viewModel.LoginViewModel.LoginViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                val loginViewModel: LoginViewModel = viewModel()

                var loginCompletado by remember {
                    mutableStateOf(false)
                }

                if (loginCompletado) {

                    UltimaMillaApp(
                        onLogout = {
                            loginCompletado = false
                        }
                    )

                } else {

                    LoginScreen(
                        viewModel = loginViewModel,
                        onNextButtonClicked = {
                            loginCompletado = true
                        }
                    )
                }
            }
        }
    }
}