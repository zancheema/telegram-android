package com.zancheema.android.telegram.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zancheema.android.telegram.data.source.local.dao.*
import com.zancheema.android.telegram.data.source.local.entity.*

@Database(
    entities = [
        DbUser::class,
        DbUserDetail::class,
        DbChatRoom::class,
        DbChatRoomMember::class,
        DbChatRoomDetail::class,
        DbChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userDetailDao(): UserDetailDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun chatRoomMemberDao(): ChatRoomMemberDao
    abstract fun chatRoomDetailDao(): ChatRoomDetailDao
    abstract fun chatMessageDao(): ChatMessageDao
}
