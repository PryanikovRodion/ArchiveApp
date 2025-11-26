package com.pryanikov.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pryanikov.data.db.dao.DocumentDao
import com.pryanikov.data.db.dao.UserDao
import com.pryanikov.data.db.entity.DocumentEntity
import com.pryanikov.data.db.entity.UserEntity

@Database(
    entities = [UserEntity::class, DocumentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun documentDao(): DocumentDao
}