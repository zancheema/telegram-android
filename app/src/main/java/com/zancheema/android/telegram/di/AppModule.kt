package com.zancheema.android.telegram.di

import android.content.Context
import androidx.room.Room
import com.zancheema.android.telegram.data.source.*
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.LocalDataSource
import com.zancheema.android.telegram.data.source.remote.Firestore
import com.zancheema.android.telegram.data.source.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

@Module
@InstallIn(SingletonComponent::class)
object AppModule  {
    @Qualifier
    @Retention(RUNTIME)
    annotation class RemoteAppDataSource

    @Qualifier
    @Retention(RUNTIME)
    annotation class LocalAppDataSource

    @Singleton
    @RemoteAppDataSource
    @Provides
    fun provideRemoteDataSource(
        firestore: Firestore,
        ioDispatcher: CoroutineDispatcher
    ): AppDataSource {
        return RemoteDataSource(firestore, ioDispatcher)
    }

    @Singleton
    @LocalAppDataSource
    @Provides
    fun provideLocalDataSource(
        database: AppDatabase,
        ioDispatcher: CoroutineDispatcher
    ): AppDataSource {
        return LocalDataSource(database, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Telegram.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Singleton
    @Provides
    fun provideFirestore(
        contentProvider: AppContentProvider
    ): Firestore {
        return Firestore(contentProvider)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppContentModule {
    @Singleton
    @Provides
    fun provideAppRepository(
        @AppModule.RemoteAppDataSource remoteDataSource: AppDataSource,
        @AppModule.LocalAppDataSource localDataSource: AppDataSource,
        ioDispatcher: CoroutineDispatcher
    ): AppRepository {
        return DefaultRepository(
            remoteDataSource, localDataSource, ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideAppContentProvider(): AppContentProvider {
        return DefaultContentProvider()
    }
}