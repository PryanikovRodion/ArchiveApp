package com.pryanikov.domain.repository

import com.pryanikov.domain.model.Document

/**
 * Інтерфейс (контракт) для репозиторію документів.
 *
 * Описує, *що* ми хочемо робити з документами,
 * але не каже, *як*.
 */
interface DocumentRepository {

    /**
     * Отримує повний список усіх документів.
     * @return [List] об'єктів [Document].
     */
    suspend fun getAllDocuments(): List<Document>

    /**
     * Отримує один конкретний документ за його ID.
     * @param id Унікальний ID документа.
     * @return [Document], якщо знайдено, або `null`, якщо ні.
     */
    suspend fun getDocumentById(id: String): Document?

    /**
     * Виконує повнотекстовий пошук по документах.
     * @param query Пошуковий запит (наприклад, частина назви, тег, автор).
     * @return [List] документів, що відповідають запиту.
     */
    suspend fun searchDocuments(query: String): List<Document>

    /**
     * Зберігає (створює новий або оновлює існуючий) документ.
     * @param document Об'єкт [Document] для збереження.
     */
    suspend fun saveDocument(document: Document)

    /**
     * Видаляє документ за його ID.
     * @param id Унікальний ID документа для видалення.
     */
    suspend fun deleteDocument(id: String)

    /**
     * Отримує список документів, доданих/змінених конкретним користувачем.
     * @param userId ID користувача.
     * @return [List] документів.
     */
    suspend fun getDocumentsByUserId(userId: String): List<Document>
}