package com.zancheema.android.telegram.di

import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.DefaultRepository
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.data.source.DefaultContentProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRepositoryModule {
    @Singleton
    @Provides
    fun provideAppRepository(): AppRepository {
        return DefaultRepository()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppContentProviderModule {
    @Singleton
    @Binds
    abstract fun provideTestContentProvider(provider: DefaultContentProvider): AppContentProvider
}
