package com.zancheema.android.telegram.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoomDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ChatRoomDetailDaoTest {

    private lateinit var database: AppDatabase
    private val chatRoom = DbChatRoom("chat_room")

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun init() = runBlockingTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        database.chatRoomDao().insertChatRoom(chatRoom)
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun insertChatRoomDetailAndGetByChatRoomId() = runBlockingTest {
        val detail = DbChatRoomDetail(chatRoom.id, "John Doe", "I'm good.")
        database.chatRoomDetailDao().insertChatRoomDetail(detail)

        val loaded = database.chatRoomDetailDao().getChatRoomDetailByChatRoomId(detail.chatRoomId)
        assertThat(loaded, `is`(notNullValue()))
        assertThat(loaded, `is`(detail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertingDetailTwiceForSameChatRoomReplacesPreviousChatRoomDetail() = runBlockingTest {
        val detail = DbChatRoomDetail(chatRoom.id, "John Doe", "I'm good.")
        database.chatRoomDetailDao().insertChatRoomDetail(detail)
        val secondDetail = DbChatRoomDetail(chatRoom.id, "Jane Doe", "OK")
        database.chatRoomDetailDao().insertChatRoomDetail(secondDetail)

        val loaded = database.chatRoomDetailDao().getChatRoomDetailByChatRoomId(detail.chatRoomId)
        assertThat(loaded, `is`(notNullValue()))
        assertThat(loaded, `is`(not(detail)))
        assertThat(loaded, `is`(secondDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deletingChatRoomAlsoDeletesChatRoomDetail() = runBlockingTest {
        val detail = DbChatRoomDetail(chatRoom.id, "John Doe", "I'm good.")
        database.chatRoomDetailDao().insertChatRoomDetail(detail)

        database.chatRoomDao().deleteChatRoom(chatRoom)

        val loaded = database.chatRoomDetailDao().getChatRoomDetailByChatRoomId(detail.chatRoomId)
        assertThat(loaded, `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateRecentMessage_GetByIdReturnsDetailWithUpdatedRecent() = runBlockingTest {
        val detail = DbChatRoomDetail(chatRoom.id, "John Doe", "I'm good.")
        database.chatRoomDetailDao().insertChatRoomDetail(detail)

        val updatedRecentMessage = "OK"
        database.chatRoomDetailDao().updateRecentMessage(detail.chatRoomId, updatedRecentMessage)

        val loaded = database.chatRoomDetailDao().getChatRoomDetailByChatRoomId(detail.chatRoomId)
        assertThat(loaded, `is`(notNullValue()))
        loaded as DbChatRoomDetail
        assertThat(loaded.chatRoomId, `is`(detail.chatRoomId))
        assertThat(loaded.name, `is`(detail.name))
        assertThat(loaded.recentMessage, `is`(updatedRecentMessage))
    }
}