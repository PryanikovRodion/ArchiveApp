package com.pryanikov.archiveapp.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pryanikov.domain.model.Document
import com.pryanikov.domain.model.User
import com.pryanikov.domain.usecase.auth.CheckAuthStatusUseCase
import com.pryanikov.domain.usecase.auth.LogoutUseCase
import com.pryanikov.domain.usecase.document.GetDocumentsUseCase
import com.pryanikov.domain.usecase.document.SearchDocumentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI-стан для головного екрана.
 * Містить все, що потрібно для відображення:
 * список документів, стан завантаження, поточного користувача тощо.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val documents: List<Document> = emptyList(),
    val searchQuery: String = "",
    val currentUser: User? = null,
    val error: String? = null,
    val logoutSuccess: Boolean = false // Тригер для навігації на екран входу
)

/**
 * ViewModel для головного екрана [HomeScreen].
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val searchDocumentsUseCase: SearchDocumentsUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Зберігаємо посилання на задачу пошуку, щоб скасовувати її
    private var searchJob: Job? = null

    init {
        // При старті ViewModel ми маємо:
        // 1. Перевірити, хто поточний користувач
        observeAuthStatus()
        // 2. Завантажити початковий список документів
        loadDocuments()
    }

    /**
     * Завантажує або перезавантажує повний список документів.
     */
    private fun loadDocuments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val docs = getDocumentsUseCase.execute()
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

    /**
     * Запускає відстеження статусу автентифікації.
     * Цей потік "живий" і повідомить нас, якщо користувач вийде з системи.
     */
    private fun observeAuthStatus() {
        checkAuthStatusUseCase.execute()
            .onEach { user ->
                if (user == null) {
                    // Користувач вийшов!
                    _uiState.update { it.copy(logoutSuccess = true, currentUser = null) }
                } else {
                    // Користувач увійшов
                    _uiState.update { it.copy(currentUser = user) }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Викликається з UI, коли користувач вводить текст у пошук.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // Скасовуємо попередню задачу пошуку, якщо вона ще не виконалась
        searchJob?.cancel()

        // Запускаємо нову задачу пошуку з "дебаунсом" (затримкою)
        searchJob = viewModelScope.launch {
            delay(300L) // Чекаємо 300 мс, перш ніж виконати пошук

            _uiState.update { it.copy(isLoading = true) }
            try {
                val results = searchDocumentsUseCase.execute(query)
                _uiState.update {
                    it.copy(isLoading = false, documents = results, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Помилка пошуку")
                }
            }
        }
    }

    /**
     * Викликається з UI, коли користувач натискає "Вийти".
     */
    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase.execute()
            // Нам не потрібно нічого робити тут,
            // 'observeAuthStatus' автоматично це помітить
            // і оновить 'logoutSuccess'.
        }
    }
}