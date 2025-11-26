package com.pryanikov.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pryanikov.domain.model.Role
import com.pryanikov.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val role: Role,         // Использует конвертер
    val passwordHash: String
) {
    fun toDomain() = User(
        id = id,
        email = email,
        name = name,
        role = role
    )
}