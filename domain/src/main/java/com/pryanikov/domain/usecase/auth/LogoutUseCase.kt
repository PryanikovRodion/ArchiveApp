package com.pryanikov.domain.usecase.auth

import com.pryanikov.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * UseCase для виходу з системи.
 */
class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Виконує вихід з системи.
     */
    suspend fun execute() {
        // Тут немає особливої бізнес-логіки,
        // тому ми просто викликаємо репозиторій.
        repository.logout()
    }
}