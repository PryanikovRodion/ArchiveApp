package com.pryanikov.archiveapp.di

import com.pryanikov.data.datasource.FakeAuthDataSource
import com.pryanikov.data.datasource.FakeLocalDocumentDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Цей модуль "навчає" Hilt, як надавати (provide)
 * наші DataSource-заглушки, які є 'object'-ами (синглтонами).
 */
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    /**
     * Надає єдиний екземпляр FakeAuthDataSource.
     */
    @Provides
    @Singleton
    fun provideFakeAuthDataSource(): FakeAuthDataSource {
        return FakeAuthDataSource
    }

    /**
     * Надає єдиний екземпляр FakeLocalDocumentDataSource.
     */
    @Provides
    @Singleton
    fun provideFakeLocalDocumentDataSource(): FakeLocalDocumentDataSource {
        return FakeLocalDocumentDataSource
    }
}