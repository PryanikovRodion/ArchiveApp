package com.pryanikov.archiveapp.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Ми припустимо, що MainScreen буде тут:
import com.pryanikov.archiveapp.view.screens.MainScreen

// Імпорт нашого sealed-класу
import com.pryanikov.archiveapp.view.navigation.Screen // <-- ДОДАНО ЦЕЙ РЯДОК

/**
 * Головний навігаційний хост додатка (зовнішня навігація).
 *
 * У нашому новому дизайні він дуже простий. Він, по суті, просто
 * запускає [MainScreen] (який ми напишемо наступним), а той вже сам вирішує,
 * чи показувати поверх себе діалог входу.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // Ми можемо безпечно стартувати з [Screen.Main.route],
        // оскільки `MainScreen` сам перевірить авторизацію
        // і покаже діалог входу, якщо потрібно.
        startDestination = Screen.Main.route // <-- Тепер помилки не буде
    ) {

        // Головний екран-контейнер
        composable(route = Screen.Main.route) { // <-- І тут теж
            MainScreen(navController = navController)
            // (Ми розкоментуємо це, коли створимо MainScreen.kt)
        }

        /*
         Зараз, згідно з новим дизайном ("все в діалогах"),
         нам не потрібні інші маршрути.

         АЛЕ, якби ми вирішили, що "Створити документ" - це
         окрема сторінка, а не діалог, ми б додали її тут:

        composable(route = Screen.CreateDocument.route) {
             // CreateDocumentScreen(navController = navController)
        }
        */
    }
}