package com.pryanikov.data.datasource

import com.pryanikov.domain.model.Document
import com.pryanikov.domain.model.Status
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate


object FakeLocalDocumentDataSource {

    private val documents = mutableListOf<Document>()
    suspend fun getAll(): List<Document> {
        delay(300)
        return documents
    }


    suspend fun getById(id: String): Document? {
        delay(100)
        return documents.find { it.id == id }
    }


    suspend fun search(query: String): List<Document> {
        delay(300) // Імітація

        // 1. Розділяємо запит на окремі слова (за вашою логікою)
        val searchWords = query.toLowerCase().trim().split(" ")
            .filter { it.isNotBlank() } // Видаляємо зайві пробіли

        if (searchWords.isEmpty()) {
            return documents // Якщо запит порожній, повертаємо все
        }

        // 4. Повертаємо список
        return documents.filter { document ->
            // 2. Створюємо "метадані" для кожного документа
            val metadataString = buildString {
                append(document.title.toLowerCase())
                append(" ")
                append(document.author.joinToString(" ").toLowerCase())
                append(" ")
                append(document.tags.joinToString(" ").toLowerCase())
                append(" ")
                append(document.type.toLowerCase())
                // 'genre' - це тег, тому він вже тут
            }

            // 3. Перевіряємо, чи ВСІ слова із запиту є в метаданих
            // (Це і є ваш AND-пошук)
            searchWords.all { word ->
                metadataString.contains(word)
            }
        }
    }


    suspend fun save(document: Document) {
        delay(200)
        val index = documents.indexOfFirst { it.id == document.id }
        if (index >= 0) {
            documents[index] = document
        } else {
            documents.add(document)
        }
    }

    suspend fun delete(id: String) {
        delay(400)
        documents.removeAll { it.id == id }
    }

    suspend fun getByUserId(userId: String): List<Document> {
        delay(200)
        return documents.filter { it.addedByUserId == userId }
    }
}