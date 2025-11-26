package com.pryanikov.domain.repository

import com.pryanikov.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Інтерфейс (контракт) для репозиторію автентифікації.
 *
 * Описує, *що* ми хочемо робити з даними користувача,
 * але не каже, *як* (це буде в data-шарі).
 */
interface AuthRepository {

    /**
     * Виконує вхід у систему.
     * @param email Email користувача.
     * @param pass Пароль.
     * @return Повертає [Result] з [User] у разі успіху, або помилку в разі невдачі.
     */
    suspend fun login(email: String, pass: String): Result<User>

    /**
     * Виконує вихід із системи.
     */
    suspend fun logout()

    /**
     * Виконує повторну автентифікацію (наприклад, для підтвердження видалення).
     * @param email Email користувача.
     * @param pass Пароль для перевірки.
     * @return Повертає [Result] з [Unit] у разі успіху, або помилку.
     */
    suspend fun reAuthenticate(email: String, pass: String): Result<Unit>

    /**
     * Повертає "гарячий" потік [Flow], який завжди містить
     * поточного користувача ([User]) або `null`, якщо сесії немає.
     *
     * ViewModel-и будуть підписуватися на цей потік, щоб знати
     * про стан автентифікації в реальному часі.
     */
    fun getCurrentUserFlow(): Flow<User?>
}