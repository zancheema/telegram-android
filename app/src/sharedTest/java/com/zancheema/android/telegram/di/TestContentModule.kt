package com.zancheema.android.telegram.di

import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.FakeContentProvider
import com.zancheema.android.telegram.data.source.FakeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TestContentModule {
    @Singleton
    @Binds
    abstract fun provideTestRepository(repository: FakeRepository): AppRepository

    @Singleton
    @Binds
    abstract fun provideTestContentProvider(provider: FakeContentProvider): AppContentProvider
}