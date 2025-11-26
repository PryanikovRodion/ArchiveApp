package com.pryanikov.domain.usecase.document

import com.pryanikov.domain.model.Role // <-- Переконайтеся, що імпорт є
import com.pryanikov.domain.repository.AuthRepository
import com.pryanikov.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * UseCase для видалення документа.
 * Вимагає повторної автентифікації ТА прав Адміністратора.
 */
class DeleteDocumentUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val authRepository: AuthRepository
) {

    /**
     * Виконує логіку видалення.
     * @param documentId ID документа, що видаляється.
     * @param password Пароль, введений користувачем для підтвердження.
     * @return [Result] з [Unit] або помилкою.
     */
    suspend fun execute(documentId: String, password: String): Result<Unit> {
        // --- Бізнес-логіка ---

        // Правило №1: Отримуємо поточного користувача.
        val currentUser = authRepository.getCurrentUserFlow().firstOrNull()
            ?: return Result.failure(IllegalStateException("Користувач не автентифікований"))

        // !!--- ПЕРЕВІРКА РОЛІ ---!!
        // Правило №2: Перевіряємо, чи має користувач право видаляти.
        // Дозволяємо видалення тільки Адміністратору (ADMIN).
        if (currentUser.role != Role.ADMIN) {
            return Result.failure(Exception("У вас немає прав на видалення документів."))
        }
        // !!--- КІНЕЦЬ ПЕРЕВІРКИ ---!!

        // Правило №3: Виконуємо повторну автентифікацію (перевірка пароля).
        val reAuthResult = authRepository.reAuthenticate(currentUser.email, password)

        if (reAuthResult.isFailure) {
            return Result.failure(Exception("Невірний пароль. Видалення скасовано."))
        }

        // Правило №4: Якщо все ОК, видаляємо документ.
        documentRepository.deleteDocument(documentId)
        return Result.success(Unit)
        // ---------------------
    }
}