package com.pryanikov.data.datasource

import com.pryanikov.domain.model.Role
import com.pryanikov.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object FakeAuthDataSource {

    private val _currentUserFlow = MutableStateFlow<User?>(null)

    private val fakeAdmin = User(
        id = "0000",
        email = "admin",
        name = "ADMIN",
        role = Role.ADMIN
    )
    private val fakeEditor = User(
        id = "1111",
        email = "alex",
        name = "ALEX",
        role = Role.EDITOR
    )
    // ------------------------------------
    suspend fun login(email: String, pass: String): Result<User> {
        delay(500)
        if (pass != "password123") {
            return Result.failure(Exception("Invalid password"))
        }

        return when (email.trim()) {
            fakeAdmin.email -> {
                _currentUserFlow.value = fakeAdmin
                Result.success(fakeAdmin)
            }
            fakeEditor.email -> {
                _currentUserFlow.value = fakeEditor
                Result.success(fakeEditor)
            }
            else -> {
                Result.failure(Exception("Invalid password"))
            }
        }
    }

    suspend fun logout() {
        delay(100)
        _currentUserFlow.value = null
    }

    suspend fun reAuthenticate(email: String, pass: String): Result<Unit> {
        delay(300)
        if (pass == "password123" && (email == fakeAdmin.email || email == fakeEditor.email)) {
            return Result.success(Unit)
        }
        return Result.failure(Exception("Неверный пароль"))
    }

    fun getCurrentUserFlow(): Flow<User?> {
        return _currentUserFlow.asStateFlow()
    }
}