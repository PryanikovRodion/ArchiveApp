package com.pryanikov.archiveapp.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pryanikov.domain.model.Document
import com.pryanikov.domain.model.Status
import com.pryanikov.domain.usecase.document.GetDocumentByIdUseCase
import com.pryanikov.domain.usecase.document.SaveDocumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.Instant

/**
 * UI-стан для діалогового вікна Додавання/Редагування.
 *
 * @param isLoading Чи йде завантаження (для режиму редагування).
 * @param isSaving Чи йде збереження.
 * @param document Поточний документ (порожній, якщо створюємо;
 * завантажений, якщо редагуємо).
 * @param error Помилка.
 * @param saveSuccess Тригер для закриття діалогу після успішного збереження.
 */
data class EditDocumentUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val document: Document? = null,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

/**
 * ViewModel для діалогового вікна "Додати/Редагувати".
 * Керує логікою завантаження документа (для редагування)
 * та збереження (для створення/редагування).
 */
@HiltViewModel
class EditDocumentViewModel @Inject constructor(
    private val getDocumentByIdUseCase: GetDocumentByIdUseCase,
    private val saveDocumentUseCase: SaveDocumentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditDocumentUiState())
    val uiState: StateFlow<EditDocumentUiState> = _uiState.asStateFlow()

    /**
     * Цей метод викликається, коли діалог відкривається.
     * @param documentId ID документа для редагування.
     * Якщо `null` - це режим СТВОРЕННЯ нового документа.
     */
    fun loadDocument(documentId: String?) {
        if (documentId == null) {
            // Режим СТВОРЕННЯ
            _uiState.update {
                it.copy(
                    isLoading = false,
                    // Створюємо "порожній" документ-шаблон
                    document = Document(
                        id = "", // ID порожній, SaveUseCase зрозуміє, що це новий
                        title = "",
                        author = emptyList(),
                        type = "Рукопис", // Значення за замовчуванням
                        status = Status.NEW,
                        addedByUserId = "", // SaveUseCase заповнить це
                        createdAt = Instant.now(), // SaveUseCase оновить це
                        updatedAt = Instant.now()  // SaveUseCase оновить це
                    )
                )
            }
        } else {
            // Режим РЕДАГУВАННЯ
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                try {
                    val doc = getDocumentByIdUseCase.execute(documentId)
                    _uiState.update { it.copy(isLoading = false, document = doc) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, error = "Не вдалося завантажити") }
                }
            }
        }
    }

    /**
     * Викликається, коли користувач змінює будь-яке поле у UI.
     * @param updatedDocument Нова версія документа з UI.
     */
    fun onDocumentChanged(updatedDocument: Document) {
        _uiState.update {
            it.copy(document = updatedDocument, error = null)
        }
    }

    /**
     * Викликається, коли користувач натискає "Зберегти".
     */
    fun onSaveClick() {
        val documentToSave = _uiState.value.document ?: return

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                // Ми просто передаємо документ.
                // SaveDocumentUseCase сам розбереться,
                // новий він (за порожнім ID) чи старий.
                saveDocumentUseCase.execute(documentToSave)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = e.message ?: "Помилка збереження")
                }
            }
        }
    }

    /**
     * Скидає стан (викликається, коли діалог закривається).
     */
    fun dismiss() {
        _uiState.value = EditDocumentUiState()
    }
}