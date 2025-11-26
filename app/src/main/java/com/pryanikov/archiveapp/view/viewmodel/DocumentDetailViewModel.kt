package com.pryanikov.archiveapp.view.viewmodel

//import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pryanikov.domain.model.Document
import com.pryanikov.domain.usecase.document.DeleteDocumentUseCase
import com.pryanikov.domain.usecase.document.GetDocumentByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI-стан для екрана деталей документа.
 *
 * @param isLoadingDocument Йде завантаження самого документа?
 * @param isDeleting Чи йде процес видалення (очікування пароля)?
 * @param document Поточний завантажений документ.
 * @param error Повідомлення про помилку.
 * @param showConfirmDeleteDialog Чи потрібно показати діалог підтвердження пароля?
 * @param deleteSuccess Тригер для навігації "назад" після успішного видалення.
 */
data class DocumentDetailUiState(
    val isLoadingDocument: Boolean = true,
    val isDeleting: Boolean = false,
    val document: Document? = null,
    val error: String? = null,
    val showConfirmDeleteDialog: Boolean = false,
    val deleteSuccess: Boolean = false
)

/**
 * ViewModel для екрана [DocumentDetailScreen].
 *
 * @param savedStateHandle Спеціальний об'єкт Hilt, який
 * дозволяє отримати параметри навігації (наприклад, ID документа).
 */
@HiltViewModel
class DocumentDetailViewModel @Inject constructor(
    private val getDocumentByIdUseCase: GetDocumentByIdUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    //private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentDetailUiState())
    val uiState: StateFlow<DocumentDetailUiState> = _uiState.asStateFlow()

    // Зберігаємо ID документа, з яким працюємо
    //private val documentId: String = savedStateHandle.get<String>("documentId") ?: ""

    //init {
    //    if (documentId.isBlank()) {
    //        _uiState.update { it.copy(isLoadingDocument = false, error = "ID документа не знайдено") }
    //    } else {
    //        loadDocument()
    //    }
    //}

    /**
     * Завантажує дані одного документа.
     */
    fun loadDocument(documentId: String?) {
        if (documentId.isNullOrBlank()) {
            _uiState.update { it.copy(isLoadingDocument = false, error = "ID документа не знайдено") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDocument = true) }
            try {
                // Використовуємо переданий ID
                val doc = getDocumentByIdUseCase.execute(documentId)
                _uiState.update {
                    it.copy(isLoadingDocument = false, document = doc, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoadingDocument = false, error = "Не вдалося завантажити документ")
                }
            }
        }
    }
    fun dismiss() {
        _uiState.value = DocumentDetailUiState()
    }

    /**
     * Викликається, коли користувач натискає "Видалити".
     * Просто відкриває діалогове вікно.
     */
    fun onDeleteClick() {
        _uiState.update { it.copy(showConfirmDeleteDialog = true, error = null) }
    }

    /**
     * Викликається, коли користувач закриває діалог.
     */
    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showConfirmDeleteDialog = false) }
    }

    /**
     * Викликається, коли користувач вводить пароль і підтверджує видалення.
     */
    fun onConfirmDelete(password: String) {
        val documentIdToUse = _uiState.value.document?.id ?: ""
        if (documentIdToUse.isBlank()){
            _uiState.update { it.copy(isDeleting = false, error = "Помилка: ID документа втрачено") }
            return
        }
        viewModelScope.launch {
            val result = deleteDocumentUseCase.execute(documentIdToUse, password)

            if (result.isSuccess) {
                // Успіх!
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        showConfirmDeleteDialog = false,
                        deleteSuccess = true // Тригер для навігації назад
                    )
                }
            } else {
                // Помилка (наприклад, невірний пароль)
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        // Залишаємо діалог відкритим, щоб показати помилку
                        showConfirmDeleteDialog = true,
                        error = result.exceptionOrNull()?.message ?: "Помилка видалення"
                    )
                }
            }
        }
    }
}