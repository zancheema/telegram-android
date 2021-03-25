package com.zancheema.android.telegram.di

import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.source.FakeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TestRepositoryModule {
    @Singleton
    @Binds
    abstract fun provideTestRepository(repository: FakeRepository): AppRepository
}