package com.pryanikov.domain.usecase.auth

import com.pryanikov.domain.model.User
import com.pryanikov.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase для перевірки (та відстеження) поточного статусу автентифікації.
 *
 * Цей UseCase надає ViewModel-ам "живий" потік [Flow]
 * зі станом користувача.
 */
class CheckAuthStatusUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Повертає потік [Flow], що "випромінює" [User],
     * якщо він увійшов до системи, або `null`, якщо ні.
     */
    fun execute(): Flow<User?> {
        // Тут немає бізнес-логіки, ми просто передаємо
        // "контракт" репозиторію далі до ViewModel.
        return repository.getCurrentUserFlow()
    }
}