package com.pryanikov.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pryanikov.data.db.entity.DocumentEntity

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents")
    suspend fun getAll(): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getById(id: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE addedByUserId = :userId")
    suspend fun getByUserId(userId: String): List<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun delete(id: String)
}