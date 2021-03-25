package com.zancheema.android.telegram.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoomMember
import com.zancheema.android.telegram.data.source.local.entity.DbUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ChatRoomMemberDaoTest {
    private lateinit var database: AppDatabase

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun insertAndGetChatRoomMembersByChatRoomId() = runBlockingTest {
        // create chat room to pass foreign key constraint
        val chatRoom = DbChatRoom("chat_room")
        database.chatRoomDao().insertChatRoom(chatRoom)
        // add users to chat room to pass foreign key constraint
        val users = listOf(
            DbUser("+14375559087"),
            DbUser("+913275559087"),
            DbUser("+174375551987")
        )
        for (u in users) database.userDao().insertUser(u)
        // insert the chat room members
        val chatRoomMembers = users.map { user ->
            DbChatRoomMember(user.phoneNumber, chatRoom.id)
        }
        for (c in chatRoomMembers) database.chatRoomMemberDao().insertChatRoomMember(c)

        // create other chat room and its members
        val otherChatRoom = DbChatRoom("other_chat_room")
        database.chatRoomDao().insertChatRoom(otherChatRoom)
        val otherUser = DbUser("+336725559870")
        database.userDao().insertUser(otherUser)
        val otherMember =
            DbChatRoomMember(otherUser.phoneNumber, otherChatRoom.id)
        database.chatRoomMemberDao().insertChatRoomMember(otherMember)

        val loaded = database.chatRoomMemberDao().getChatRoomMembersByChatRoomId(chatRoom.id)

        assertThat(loaded.size, `is`(chatRoomMembers.size))
        assertThat(loaded, `is`(chatRoomMembers))
        assertThat(loaded.contains(otherMember), `is`(false))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertAndGetChatRoomMembersByPhoneNumber() = runBlockingTest {
        val chatRoom1 = DbChatRoom("chat_room_1")
        val chatRoom2 = DbChatRoom("chat_room_2")
        val user = DbUser("+14375559087")
        val member1 = DbChatRoomMember(user.phoneNumber, chatRoom1.id)
        val member2 = DbChatRoomMember(user.phoneNumber, chatRoom2.id)
        database.apply {
            chatRoomDao().insertChatRoom(chatRoom1)
            chatRoomDao().insertChatRoom(chatRoom2)
            userDao().insertUser(user)
            chatRoomMemberDao().insertChatRoomMember(member1)
            chatRoomMemberDao().insertChatRoomMember(member2)
        }

        val loaded = database.chatRoomMemberDao().getChatRoomMembersByPhoneNumber(user.phoneNumber)
        assertThat(loaded.size, `is`(2))
        assertThat(loaded.contains(member1), `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deletingChatRoomDeletesAllMembersOfThatChatRoom() = runBlockingTest {
        // create chat room to pass foreign key constraint
        val chatRoom = DbChatRoom("chat_room")
        database.chatRoomDao().insertChatRoom(chatRoom)
        // add users to chat room to pass foreign key constraint
        val users = listOf(
            DbUser("+14375559087"),
            DbUser("+913275559087"),
            DbUser("+174375551987")
        )
        for (u in users) database.userDao().insertUser(u)
        // insert the chat room members
        val chatRoomMembers = users.map { user ->
            DbChatRoomMember(user.phoneNumber, chatRoom.id)
        }
        for (c in chatRoomMembers) database.chatRoomMemberDao().insertChatRoomMember(c)

        database.chatRoomDao().deleteChatRoom(chatRoom)

        val loaded = database.chatRoomMemberDao().getChatRoomMembersByChatRoomId(chatRoom.id)
        assertThat(loaded.size, `is`(0))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deletingUserDeletesAllChatRoomMembersWithItsPhoneNumbers() = runBlockingTest {
        val chatRoom1 = DbChatRoom("chat_room_1")
        val chatRoom2 = DbChatRoom("chat_room_2")
        val user = DbUser("+14375559087")
        val chatRoomMember1 = DbChatRoomMember(user.phoneNumber, chatRoom1.id)
        val chatRoomMember2 = DbChatRoomMember(user.phoneNumber, chatRoom2.id)
        database.apply {
            chatRoomDao().insertChatRoom(chatRoom1)
            chatRoomDao().insertChatRoom(chatRoom2)
            userDao().insertUser(user)
            chatRoomMemberDao().insertChatRoomMember(chatRoomMember1)
            chatRoomMemberDao().insertChatRoomMember(chatRoomMember2)
        }

        database.userDao().deleteUser(user)

        val loaded = database.chatRoomMemberDao().getChatRoomMembersByPhoneNumber(user.phoneNumber)
        assertThat(loaded.size, `is`(0))
    }
}