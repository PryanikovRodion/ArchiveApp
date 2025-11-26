package com.pryanikov.archiveapp.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pryanikov.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI-стан для екрана входу.
 * Це data class, що містить *все*, що UI потрібно знати:
 * що введено в поля, чи йде завантаження, яка помилка.
 */
data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false // Тригер для навігації
)

/**
 * ViewModel для екрана входу [LoginScreen].
 *
 * @HiltViewModel каже Hilt, що цей клас є ViewModel і Hilt має
 * вміти його створювати та "впроваджувати" в нього залежності.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    // Приватний, змінний стан
    private val _uiState = MutableStateFlow(LoginUiState())
    // Публічний, незмінний (read-only) стан для UI
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Викликається з UI, коли користувач вводить email.
     */
    fun onEmailChanged(email: String) {
        _uiState.update { currentState ->
            currentState.copy(email = email, error = null)
        }
    }

    /**
     * Викликається з UI, коли користувач вводить пароль.
     */
    fun onPasswordChanged(pass: String) {
        _uiState.update { currentState ->
            currentState.copy(pass = pass, error = null)
        }
    }

    /**
     * Викликається з UI, коли користувач натискає кнопку "Увійти".
     */
    fun onLoginClick() {
        // Отримуємо поточні значення email і pass зі стану
        val email = _uiState.value.email
        val pass = _uiState.value.pass

        // Встановлюємо стан "Завантаження"
        _uiState.update { it.copy(isLoading = true, error = null) }

        // Запускаємо корутину в скоупі ViewModel
        viewModelScope.launch {
            // Викликаємо наш UseCase
            val result = loginUseCase.execute(email, pass)

            if (result.isSuccess) {
                // Успіх!
                _uiState.update {
                    it.copy(isLoading = false, loginSuccess = true)
                }
            } else {
                // Невдача!
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Невідома помилка"
                    )
                }
            }
        }
    }

    /**
     * Викликається UI після того, як він виконав навігацію.
     * Скидає тригер [loginSuccess], щоб уникнути повторної навігації
     * (наприклад, при повороті екрану).
     */
    fun onLoginSuccessNavigated() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}