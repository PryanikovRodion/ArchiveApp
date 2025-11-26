package com.pryanikov.data.repository

import com.pryanikov.data.db.dao.DocumentDao
import com.pryanikov.data.db.entity.DocumentEntity
import com.pryanikov.domain.model.Document
import com.pryanikov.domain.repository.DocumentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val dao: DocumentDao
) : DocumentRepository {

    override suspend fun getAllDocuments(): List<Document> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun getDocumentById(id: String): Document? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun searchDocuments(query: String): List<Document> {

        val allDocs = dao.getAll().map { it.toDomain() }


        val searchWords = query.lowercase().trim().split(" ").filter { it.isNotBlank() }
        if (searchWords.isEmpty()) {
            return allDocs
        }


        return allDocs.filter { document ->
            val metadataString = buildString {
                append(document.title.lowercase())
                append(" ")
                append(document.author.joinToString(" ").lowercase())
                append(" ")
                append(document.tags.joinToString(" ").lowercase())
                append(" ")
                append(document.type.lowercase())
            }


            searchWords.all { word -> metadataString.contains(word) }
        }
    }

    override suspend fun saveDocument(document: Document) {
        dao.insert(DocumentEntity.fromDomain(document))
    }

    override suspend fun deleteDocument(id: String) {
        dao.delete(id)
    }

    override suspend fun getDocumentsByUserId(userId: String): List<Document> {
        return dao.getByUserId(userId).map { it.toDomain() }
    }
}