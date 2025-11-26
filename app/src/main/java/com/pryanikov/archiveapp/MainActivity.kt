package com.pryanikov.archiveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pryanikov.archiveapp.view.navigation.AppNavHost
import com.pryanikov.archiveapp.ui.theme.ArchiveAppTheme // Переконайтеся, що імпорт теми правильний
import dagger.hilt.android.AndroidEntryPoint

/**
 * Головна і єдина Activity у нашому додатку.
 *
 * @AndroidEntryPoint "вмикає" Hilt для цієї Activity та всіх Composable-функцій,
 * які будуть в ній викликані (це дозволяє ViewModel-ам інжектуватися).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ArchiveAppTheme - це ваша стандартна тема Compose
            ArchiveAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Запускаємо наш головний навігатор!
                    AppNavHost()
                }
            }
        }
    }
}