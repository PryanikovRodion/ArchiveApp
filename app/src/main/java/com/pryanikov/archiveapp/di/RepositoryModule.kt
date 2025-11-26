package com.pryanikov.archiveapp.di

import com.pryanikov.data.repository.AuthRepositoryImpl
import com.pryanikov.data.repository.DocumentRepositoryImpl
import com.pryanikov.domain.repository.AuthRepository
import com.pryanikov.domain.repository.DocumentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt-модуль, який "навчає" Hilt, які саме реалізації
 * потрібно використовувати, коли хтось запитує інтерфейс репозиторію.
 *
 * @InstallIn(SingletonComponent::class) означає, що всі ці залежності
 * будуть "синглтонами" і житимуть протягом усього життя додатка.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Цей метод "зв'язує" інтерфейс [AuthRepository] з його
     * конкретною реалізацією [com.pryanikov.data.data.repository.AuthRepositoryImpl].
     *
     * Коли UseCase або ViewModel попросить Hilt: "Дай мені AuthRepository",
     * Hilt скаже: "Добре, ось тобі екземпляр AuthRepositoryImpl".
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    /**
     * Цей метод "зв'язує" інтерфейс [DocumentRepository] з його
     * конкретною реалізацією [com.pryanikov.data.data.repository.DocumentRepositoryImpl].
     */
    @Binds
    @Singleton
    abstract fun bindDocumentRepository(
        documentRepositoryImpl: DocumentRepositoryImpl
    ): DocumentRepository
}