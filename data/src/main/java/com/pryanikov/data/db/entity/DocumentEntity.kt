package com.pryanikov.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pryanikov.domain.model.Document
import com.pryanikov.domain.model.Status
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: List<String>,
    val type: String,
    val status: Status,
    val addedByUserId: String,


    val createdAt: Instant,
    val updatedAt: Instant,
    val documentDate: LocalDate?,


    val sourceIds: List<String>,
    val description: String?,
    val tags: List<String>,
    val fileUrl: String?
) {
    // Из БД в Domain
    fun toDomain() = Document(
        id = id,
        title = title,
        author = author,
        type = type,
        status = status,
        addedByUserId = addedByUserId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        documentDate = documentDate,
        sourceIds = sourceIds,
        description = description,
        tags = tags,
        fileUrl = fileUrl
    )

    companion object {

        fun fromDomain(doc: Document) = DocumentEntity(
            id = doc.id,
            title = doc.title,
            author = doc.author,
            type = doc.type,
            status = doc.status,
            addedByUserId = doc.addedByUserId,
            createdAt = doc.createdAt,
            updatedAt = doc.updatedAt,
            documentDate = doc.documentDate,
            sourceIds = doc.sourceIds,
            description = doc.description,
            tags = doc.tags,
            fileUrl = doc.fileUrl
        )
    }
}