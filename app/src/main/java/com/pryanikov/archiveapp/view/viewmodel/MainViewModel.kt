package com.pryanikov.archiveapp.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pryanikov.domain.model.User
import com.pryanikov.domain.usecase.auth.CheckAuthStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.pryanikov.domain.usecase.auth.LogoutUseCase
import kotlinx.coroutines.launch
/**
 * Перерахування, що описує, який саме діалог
 * (ModalBottomSheet) має бути відкритий.
 */
enum class SheetContent {
    NONE,       // Жоден (всі закриті)
    LOGIN,      // Діалог входу
    DETAILS,    // Діалог з деталями документа
    EDIT        // Діалог редагування/створення
}

/**
 * Перерахування, що описує, який пункт
 * у бічній навігації обрано.
 */
enum class MainNavItem {
    ALL_DOCUMENTS,
    MY_DOCUMENTS
}

/**
 * UI-стан для [MainScreen].
 * Описує *все*, що відбувається на головному екрані.
 *
 * @param currentUser Поточний користувач (або null).
 * @param isAuthLoading Чи йде початкова перевірка автентифікації.
 * @param currentSheet Який діалог (BottomSheet) зараз відкритий.
 * @param selectedNavItem Який пункт навігації обрано (Всі/Мої).
 * @param selectedDocumentId ID документа, який було обрано
 * (для передачі у діалоги Details/Edit).
 */
data class MainUiState(
    val currentUser: User? = null,
    val isAuthLoading: Boolean = true,
    val currentSheet: SheetContent = SheetContent.NONE,
    val selectedNavItem: MainNavItem = MainNavItem.ALL_DOCUMENTS,
    val selectedDocumentId: String? = null
)

/**
 * ViewModel для [MainScreen].
 *
 * Ця ViewModel є "диригентом" головного екрана.
 * Вона *не* завантажує списки документів (це робить HomeViewModel),
 * але вона керує станом автентифікації та тим,
 * який діалог (BottomSheet) має бути показаний.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Починаємо стежити за станом входу,
        // як тільки ViewModel створено
        observeAuthStatus()
    }

    /**
     * Запускає "живе" відстеження сесії користувача.
     */
    private fun observeAuthStatus() {
        checkAuthStatusUseCase.execute()
            .onEach { user ->
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        isAuthLoading = false, // Перевірка завершена
                        // Якщо користувача немає (null) І ми ще не
                        // в процесі входу, примусово відкриваємо діалог входу.
                        currentSheet = if (user == null && it.currentSheet != SheetContent.LOGIN) {
                            SheetContent.LOGIN
                        } else if (user != null && it.currentSheet == SheetContent.LOGIN) {
                            // Користувач успішно увійшов, закриваємо діалог
                            SheetContent.NONE
                        } else {
                            // В інших випадках нічого не змінюємо
                            it.currentSheet
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Викликається, коли користувач натискає на документ у списку.
     */
    fun onDocumentClick(documentId: String) {
        _uiState.update {
            it.copy(
                currentSheet = SheetContent.DETAILS,
                selectedDocumentId = documentId
            )
        }
    }

    /**
     * Викликається, коли користувач натискає кнопку "Додати" (FAB).
     */
    fun onAddDocumentClick() {
        _uiState.update {
            it.copy(
                currentSheet = SheetContent.EDIT,
                selectedDocumentId = null // 'null' означає "створити новий"
            )
        }
    }

    /**
     * Викликається з діалогу "Деталі", коли користувач натискає "Редагувати".
     */
    fun onEditDocumentClick() {
        // ID документа вже збережено у selectedDocumentId
        _uiState.update { it.copy(currentSheet = SheetContent.EDIT) }
    }

    /**
     * Викликається, коли користувач натискає на пункт бічної навігації.
     */
    fun onNavItemClick(item: MainNavItem) {
        _uiState.update { it.copy(selectedNavItem = item) }
    }

    /**
     * Викликається, коли будь-який діалог (BottomSheet) закривається.
     */
    fun onDismissSheet() {
        _uiState.update { it.copy(currentSheet = SheetContent.NONE) }
    }

    /**
     * Викликається, коли користувач натискає "Вихід" у бічній панелі.
     */
    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase.execute()
            // Нам не потрібно нічого робити; 'observeAuthStatus'
            // автоматично помітить зміну і оновить стан
            // (що призведе до показу діалогу входу).
        }
    }
}