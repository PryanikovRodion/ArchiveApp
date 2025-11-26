package com.pryanikov.archiveapp.view.navigation

/**
 * Sealed-клас, що визначає маршрути **ЗОВНІШНЬОГО** графа навігації.
 * Тобто, основні "зупинки" у додатку.
 */
sealed class Screen(val route: String) {

    /**
     * Екран входу в систему.
     * (Хоча він і буде діалогом, ми залишаємо його тут
     * про всяк випадок, якщо знадобиться окрема сторінка).
     */
    object Login : Screen("login_screen")

    /**
     * Головний екран-контейнер ("оболонка"),
     * який містить бічну навігаційну панель і свою
     * *внутрішню* навігацію ("Всі документи", "Мої" тощо).
     */
    object Main : Screen("main_screen") // <-- Ось той 'Main', який потрібен AppNavHost

    /**
     * Екран деталей документа.
     *
     * Зверніть увагу на `{documentId}` - це аргумент, який ми
     * будемо передавати при навігації.
     */
    object DocumentDetail : Screen("document_detail_screen/{documentId}") {
        /**
         * Допоміжна функція для створення "чистого" маршруту
         * з реальним ID.
         */
        fun createRoute(documentId: String) = "document_detail_screen/$documentId"
    }

    /**
     * Екран створення/редагування документа.
     */
    object CreateDocument : Screen("create_document_screen")
}