package com.pryanikov.domain.usecase.document

import com.pryanikov.domain.model.Document
import com.pryanikov.domain.repository.DocumentRepository
import javax.inject.Inject

/**
 * UseCase для отримання повного списку документів.
 */
class GetDocumentsUseCase @Inject constructor(
    private val repository: DocumentRepository
) {

    /**
     * Виконує логіку отримання всіх документів.
     * @return [List] об'єктів [Document].
     */
    suspend fun execute(): List<Document> {
        // У цьому простому випадку бізнес-логіка відсутня.
        // Ми просто передаємо запит до репозиторію.
        // У майбутньому тут можна додати логіку сортування за замовчуванням.
        return repository.getAllDocuments()
    }
}