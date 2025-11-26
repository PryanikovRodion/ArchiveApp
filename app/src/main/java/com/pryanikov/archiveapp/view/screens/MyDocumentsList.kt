package com.pryanikov.archiveapp.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// HiltViewModel більше не потрібен
// import androidx.hilt.navigation.compose.hiltViewModel
import com.pryanikov.archiveapp.view.viewmodel.MyDocumentsUiState

/**
 * Composable-функція, що відображає список "Мої документи".
 *
 * Ця функція стала "дурною": вона не має власної ViewModel,
 * а просто відображає стан [state], який їй передали.
 *
 * @param state Текущее состояние UI (список, загрузка, ошибка).
 * @param onDocumentClick Лямбда-функція, що викликається при натисканні.
 */
@Composable
fun MyDocumentsList(
    // Ми більше не використовуємо hiltViewModel()
    // viewModel: MyDocumentsViewModel = hiltViewModel(),

    // Натомість ми приймаємо готовий стан
    state: MyDocumentsUiState,
    onDocumentClick: (documentId: String) -> Unit
) {
    // Ми більше не підписуємося на state
    // val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            // 1. Показуємо завантаження
            CircularProgressIndicator()
        } else if (state.error != null) {
            // 2. Показуємо помилку
            val errorMessage = state.error
            if (errorMessage != null) {
                Text(text = errorMessage)
            }
        } else if (state.documents.isEmpty()) {
            // 3. Список порожній
            Text(text = "Ви ще не додали жодного документа")
        } else {
            // 4. Показуємо список документів
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = state.documents,
                    key = { document -> document.id } // Ключ для оптимізації
                ) { document ->
                    DocumentItem(
                        document = document,
                        onClick = {
                            onDocumentClick(document.id)
                        }
                    )
                }
            }
        }
    }
}