package com.pryanikov.domain.usecase.auth

import com.pryanikov.domain.model.User
import com.pryanikov.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * UseCase для входу в систему.
 * Містить бізнес-логіку, пов'язану з процесом входу.
 */
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Виконує логіку входу.
     * @param email Email користувача.
     * @param pass Пароль.
     * @return [Result] з [User] або помилкою.
     */
    suspend fun execute(email: String, pass: String): Result<User> {
        // --- Бізнес-логіка ---
        // Правило №1: Email та пароль не можуть бути порожніми.
        if (email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Email та пароль не можуть бути порожніми"))
        }

        // Правило №2: Можна додати перевірку email на валідність (regex) тут.
        // ...

        // Правило №3: Приводимо email до нижнього регістру перед відправкою.
        val processedEmail = email.toLowerCase().trim()
        // ---------------------

        // Делегуємо фактичний запит репозиторію
        return repository.login(processedEmail, pass)
    }
}