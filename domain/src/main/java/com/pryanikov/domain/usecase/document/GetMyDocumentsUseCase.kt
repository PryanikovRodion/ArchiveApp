package com.pryanikov.domain.usecase.document

import com.pryanikov.domain.model.Document
import com.pryanikov.domain.repository.AuthRepository
import com.pryanikov.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * UseCase для отримання документів,
 * пов'язаних з *поточним* автентифікованим користувачем.
 */
class GetMyDocumentsUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val authRepository: AuthRepository // Потрібен, щоб знати, хто "я"
) {

    /**
     * Виконує логіку:
     * 1. Знаходить поточного користувача.
     * 2. Запитує документи за його ID.
     *
     * @return [List] документів поточного користувача або порожній список.
     */
    suspend fun execute(): List<Document> {
        // 1. Дізнаємося, хто "я", взявши перше значення з потоку сесії
        val currentUser = authRepository.getCurrentUserFlow().firstOrNull()
            ?: return emptyList() // Якщо користувача немає, повертаємо порожній список

        // 2. Запитуємо документи для цього ID
        // Наша логіка "додані АБО редаговані" буде в DocumentRepositoryImpl
        return documentRepository.getDocumentsByUserId(currentUser.id)
        //return documentRepository.getDocumentsByUserId("0000")
    }
}