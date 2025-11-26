package com.pryanikov.domain.usecase.document

import com.pryanikov.domain.model.Document
import com.pryanikov.domain.repository.DocumentRepository
import javax.inject.Inject

/**
 * UseCase для отримання одного документа за його ID.
 */
class GetDocumentByIdUseCase @Inject constructor(
    private val repository: DocumentRepository
) {

    /**
     * Виконує логіку отримання одного документа.
     * @param id Унікальний ID документа.
     * @return [Document], якщо знайдено, або `null`.
     */
    suspend fun execute(id: String): Document? {
        // --- Бізнес-логіка ---
        // Правило №1: ID не може бути порожнім.
        if (id.isBlank()) {
            return null
        }
        // ---------------------

        return repository.getDocumentById(id)
    }
}