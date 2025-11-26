package com.pryanikov.data.repository

import com.pryanikov.data.db.dao.UserDao
import com.pryanikov.domain.model.User
import com.pryanikov.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {


    private val _currentUserFlow = MutableStateFlow<User?>(null)

    override suspend fun login(email: String, pass: String): Result<User> {
        return try {

            val userEntity = userDao.getUserByEmail(email)


            if (userEntity != null && userEntity.passwordHash == pass) {
                val user = userEntity.toDomain()
                _currentUserFlow.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Неверный логин или пароль"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        _currentUserFlow.value = null
    }

    override suspend fun reAuthenticate(email: String, pass: String): Result<Unit> {
        val userEntity = userDao.getUserByEmail(email)
        return if (userEntity != null && userEntity.passwordHash == pass) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Ошибка повторной проверки"))
        }
    }

    override fun getCurrentUserFlow(): Flow<User?> {
        return _currentUserFlow.asStateFlow()
    }
}