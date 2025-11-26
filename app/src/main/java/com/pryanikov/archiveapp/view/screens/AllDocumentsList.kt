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
// HiltViewModel больше не нужен
// import androidx.hilt.navigation.compose.hiltViewModel
import com.pryanikov.archiveapp.view.viewmodel.HomeUiState
import com.pryanikov.domain.model.Document

/**
 * Composable-функция, что отображает список *всех* документов.
 *
 * Эта функция стала "глупой": она не имеет собственной ViewModel,
 * а просто отображает состояние [state], которое ей передали.
 *
 * @param state Текущее состояние UI (список, загрузка, ошибка).
 * @param onDocumentClick Лямбда-функция, что вызывается при нажатии.
 */
@Composable
fun AllDocumentsList(

    state: HomeUiState,
    onDocumentClick: (documentId: String) -> Unit
) {


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {

            CircularProgressIndicator()
        } else if (state.error != null) {

            val errorMessage = state.error
            Text(text = errorMessage)

        } else if (state.documents.isEmpty()) {

            Text(text = "Документы не найдены")
        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = state.documents,
                    key = { document -> document.id }
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