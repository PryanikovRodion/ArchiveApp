package com.pryanikov.domain.usecase.auth

import com.pryanikov.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * UseCase для повторної автентифікації.
 * Потрібен для чутливих операцій, наприклад, видалення.
 */
class ReAuthenticateUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Виконує логіку повторної автентифікації.
     * @param email Email користувача (ми візьмемо його з сесії).
     * @param pass Пароль, який ввів користувач для підтвердження.
     * @return [Result] з [Unit] або помилкою.
     */
    suspend fun execute(email: String, pass: String): Result<Unit> {
        // --- Бізнес-логіка ---
        if (pass.isBlank()) {
            return Result.failure(Exception("Пароль не може бути порожнім"))
        }
        // ---------------------

        return repository.reAuthenticate(email, pass)
    }
}