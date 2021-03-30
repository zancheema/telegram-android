package com.zancheema.android.telegram.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zancheema.android.telegram.data.source.local.dao.*
import com.zancheema.android.telegram.data.source.local.entity.*
import com.zancheema.android.telegram.data.source.local.view.DbChat

@Database(
    entities = [
        DbUser::class,
        DbUserDetail::class,
        DbChatRoom::class,
        DbChatMessage::class
    ],
    views = [DbChat::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userDetailDao(): UserDetailDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun chatMessageDao(): ChatMessageDao
}
