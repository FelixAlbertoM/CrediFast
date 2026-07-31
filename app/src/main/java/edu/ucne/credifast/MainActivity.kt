package edu.ucne.credifast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.credifast.domain.auth.usecase.GetCurrentUserUseCase
import edu.ucne.credifast.presentation.navigation.CrediFastNavHost
import edu.ucne.credifast.ui.theme.CrediFastTheme
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isLoggedIn = getCurrentUserUseCase.isLoggedIn()

        setContent {
            CrediFastTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CrediFastNavHost(isLoggedIn = isLoggedIn)
                }
            }
        }
    }
}

