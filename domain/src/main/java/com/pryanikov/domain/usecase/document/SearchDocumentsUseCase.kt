package com.pryanikov.domain.usecase.document

import com.pryanikov.domain.model.Document
import com.pryanikov.domain.repository.DocumentRepository
import javax.inject.Inject

/**
 * UseCase для пошуку документів.
 */
class SearchDocumentsUseCase @Inject constructor(
    private val repository: DocumentRepository
) {

    /**
    L виконує логіку пошуку.
     * @param query Пошуковий запит.
     * @return [List] знайдених [Document].
     */
    suspend fun execute(query: String): List<Document> {
        // --- Бізнес-логіка ---
        // Правило №1: Не шукати за надто коротким запитом (менше 2 символів).
        if (query.length < 2) {
            // Можна повернути порожній список
            return emptyList()
            // Або, якщо запит порожній, ми можемо повернути всі документи.
            // Давайте зробимо так: якщо зовсім порожньо - повертаємо все.
            // if (query.isBlank()) {
            //    return repository.getAllDocuments()
            // }
        }

        // Правило №2: Завжди шукати у нижньому регістрі.
        val processedQuery = query.lowercase().trim()
        // ---------------------

        return repository.searchDocuments(processedQuery)
    }
}