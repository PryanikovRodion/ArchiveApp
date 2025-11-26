package com.pryanikov.domain.usecase.document // Переконайтеся, що 'package' правильний

import com.pryanikov.domain.model.Document
import com.pryanikov.domain.model.Status
import com.pryanikov.domain.repository.AuthRepository
import com.pryanikov.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

/**
 * UseCase для збереження (створення або оновлення) документа.
 * Містить логіку ВАЛІДАЦІЇ.
 */

class SaveDocumentUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val authRepository: AuthRepository
) {

    /**
     * Виконує логіку валідації та збереження.
     * @param documentToSave Документ, який потрібно зберегти.
     * @throws IllegalArgumentException Якщо поля заповнені невірно.
     * @throws IllegalStateException Якщо документ з такою назвою вже існує.
     */
    suspend fun execute(documentToSave: Document) {
        // --- БІЗНЕС-ЛОГІКА ТА ВАЛІДАЦІЯ ---

        // Правило №1: Назва не може бути порожньою
        if (documentToSave.title.isBlank()) {
            throw IllegalArgumentException("Назва не може бути порожньою")
        }

        // Правило №2: Має бути хоча б один автор
        // (Перевіряємо, що список не порожній І що він не складається з порожніх рядків)
        if (documentToSave.author.isEmpty() || documentToSave.author.all { it.isBlank() }) {
            throw IllegalArgumentException("Потрібен хоча б один автор")
        }

        // Правило №3: Перевірка на дублікат назви (тільки якщо це не той самий документ)
        val allDocs = documentRepository.getAllDocuments()
        val duplicate = allDocs.find {
            // Шукаємо документ з такою ж назвою (без урахування регістру)
            it.title.equals(documentToSave.title, ignoreCase = true) &&
                    // І переконуємося, що це не той самий документ (при редагуванні)
                    it.id != documentToSave.id
        }
        if (duplicate != null) {
            throw IllegalStateException("Документ з такою назвою вже існує")
        }

        // --- КІНЕЦЬ ВАЛІДАЦІЇ ---

        // --- Логіка Створення vs Оновлення ---
        if (documentToSave.id.isBlank()) {
            // Це новий документ.
            // 1. Потрібно дізнатися, хто його створює.
            val currentUser = authRepository.getCurrentUserFlow().firstOrNull()
                ?: throw IllegalStateException("Неможливо створити документ без користувача")

            // 2. Збагачуємо модель даними, яких немає у UI.
            val newDocument = documentToSave.copy(
                id = UUID.randomUUID().toString(), // Генеруємо унікальний ID
                addedByUserId = currentUser.id,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
                // Статус (наприклад, Status.NEW) вже має бути
                // встановлений у EditDocumentViewModel при створенні "порожнього" документа
            )
            documentRepository.saveDocument(newDocument)
        } else {
            // Це оновлення існуючого документа.
            // 1. Отримуємо поточну версію з бази (щоб не втратити createdAt).
            val existingDocument = documentRepository.getDocumentById(documentToSave.id)
                ?: throw IllegalStateException("Документ для оновлення не знайдено")

            // 2. Оновлюємо, але зберігаємо системні поля.
            val updatedDocument = documentToSave.copy(
                addedByUserId = existingDocument.addedByUserId, // Зберігаємо, хто створив
                createdAt = existingDocument.createdAt,       // Зберігаємо дату створення
                updatedAt = Instant.now()                   // Оновлюємо дату зміни
            )
            documentRepository.saveDocument(updatedDocument)
        }
    }
}