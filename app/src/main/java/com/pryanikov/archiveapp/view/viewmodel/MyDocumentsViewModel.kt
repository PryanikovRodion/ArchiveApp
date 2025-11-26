package com.pryanikov.archiveapp.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pryanikov.domain.model.Document
import com.pryanikov.domain.usecase.document.GetMyDocumentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI-стан для екрана "Мої документи".
 */
data class MyDocumentsUiState(
    val isLoading: Boolean = true,
    val documents: List<Document> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel для екрана "Мої документи".
 */
@HiltViewModel
class MyDocumentsViewModel @Inject constructor(
    private val getMyDocumentsUseCase: GetMyDocumentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyDocumentsUiState())
    val uiState: StateFlow<MyDocumentsUiState> = _uiState.asStateFlow()

    //init {
    //    // Завантажуємо документи, як тільки ViewModel створено
    //    loadMyDocuments()
    //}

    /**
     * Завантажує список документів, пов'язаних
     * з поточним користувачем.
     */
    fun loadMyDocuments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Викликаємо UseCase, який сам знайде
                // ID поточного користувача і поверне його документи.
                val docs = getMyDocumentsUseCase.execute()
                _uiState.update {
                    it.copy(isLoading = false, documents = docs, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Не вдалося завантажити документи")
                }
            }
        }
    }
}
