package com.zancheema.android.telegram.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.ChatRoom
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID.randomUUID

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {

    private lateinit var database: AppDatabase
    private lateinit var ioDispatcher: CoroutineDispatcher

    // Class under test
    private lateinit var localDataSource: LocalDataSource

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        ioDispatcher = Dispatchers.Main

        localDataSource = LocalDataSource(database, ioDispatcher)
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetUsers() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764"),
            User("+193445556732")
        )
        for (u in users) localDataSource.saveUser(u)

        val loaded = localDataSource.getUsers()
        assertThat(loaded, `is`(users))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetUserDetails() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val userDetails = listOf(
            UserDetail(users[0].phoneNumber, "John", "Doe"),
            UserDetail(users[1].phoneNumber, "Jane", "Doe")
        )
        for (u in users) localDataSource.saveUser(u)
        for (d in userDetails) localDataSource.saveUserDetail(d)

        val loaded = localDataSource.getUserDetails()
        assertThat(loaded, `is`(userDetails))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetUserDetailsByPhoneNumbers() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764"),
            User("+193445556732")
        )
        val userDetails = listOf(
            UserDetail(users[0].phoneNumber, "John", "Doe"),
            UserDetail(users[1].phoneNumber, "Jane", "Doe"),
            UserDetail(users[2].phoneNumber, "Mike", "Doe")
        )
        for (u in users) localDataSource.saveUser(u)
        for (d in userDetails) localDataSource.saveUserDetail(d)

        val selectedUserDetails = userDetails.subList(0, 2) // first two users
        val selectedPhoneNumbers: List<String> = selectedUserDetails.map { it.phoneNumber }

        val loaded = localDataSource.getUserDetails(selectedPhoneNumbers)
        assertThat(loaded, `is`(selectedUserDetails))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetUserDetailByPhoneNumber() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val userDetails = listOf(
            UserDetail(users[0].phoneNumber, "John", "Doe"),
            UserDetail(users[1].phoneNumber, "Jane", "Doe")
        )
        for (u in users) localDataSource.saveUser(u)
        for (d in userDetails) localDataSource.saveUserDetail(d)

        val selectedUserDetail = userDetails[1]
        val loaded = localDataSource.getUserDetail(selectedUserDetail.phoneNumber)
        assertThat(loaded, `is`(selectedUserDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun isRegisteredReturnsWhetherThatUserDetailIsRegistered() = runBlockingTest {
        val user = User("+12345556732")
        val userDetail = UserDetail(user.phoneNumber, "John", "Doe")
        localDataSource.saveUser(user)

        assertThat(localDataSource.isRegistered(user.phoneNumber), `is`(false))

        localDataSource.saveUserDetail(userDetail)
        assertThat(localDataSource.isRegistered(user.phoneNumber), `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetAllChatRooms() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val chatRooms = listOf(
            ChatRoom("cr_1", users[0].phoneNumber),
            ChatRoom("cr_2", users[1].phoneNumber)
        )
        // Save users to meet db foreign key constraint
        for (u in users) localDataSource.saveUser(u)
        for (c in chatRooms) localDataSource.saveChatRoom(c)

        val loaded = localDataSource.getChatRooms()
        assertThat(loaded, `is`(chatRooms))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetChatRoomById() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val chatRooms = listOf(
            ChatRoom("cr_1", users[0].phoneNumber),
            ChatRoom("cr_2", users[1].phoneNumber)
        )
        // Save users to meet db foreign key constraint
        for (u in users) localDataSource.saveUser(u)
        for (c in chatRooms) localDataSource.saveChatRoom(c)

        val selectedChatRoom = chatRooms[0]
        val loaded = localDataSource.getChatRoom(selectedChatRoom.id)
        assertThat(loaded, `is`(selectedChatRoom))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetChatMessages() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val chatRoom1 = ChatRoom("cr_1", users[0].phoneNumber)
        val chatRoom2 = ChatRoom("cr_2", users[1].phoneNumber)
        val chatRooms = listOf(chatRoom1, chatRoom2)
        val chatRoom1Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hey"),
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hello"),
        )
        val chatRoom2Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom2.id, "What's up"),
            ChatMessage(randomUUID().toString(), chatRoom2.id, "Good, what about you?"),
        )
        val allMessages = mutableListOf<ChatMessage>()
        allMessages.addAll(chatRoom1Messages)
        allMessages.addAll(chatRoom2Messages)
        for (u in users) localDataSource.saveUser(u)
        for (c in chatRooms) localDataSource.saveChatRoom(c)
        for (m in allMessages) localDataSource.saveChatMessage(m)

        val loaded = localDataSource.getChatMessages()
        assertThat(loaded, `is`(allMessages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetChatMessagesByChatRoomId() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val chatRoom1 = ChatRoom("cr_1", users[0].phoneNumber)
        val chatRoom2 = ChatRoom("cr_2", users[1].phoneNumber)
        val chatRooms = listOf(chatRoom1, chatRoom2)
        val chatRoom1Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hey"),
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hello"),
        )
        val chatRoom2Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom2.id, "What's up"),
            ChatMessage(randomUUID().toString(), chatRoom2.id, "Good, what about you?"),
        )
        val allMessages = mutableListOf<ChatMessage>()
        allMessages.addAll(chatRoom1Messages)
        allMessages.addAll(chatRoom2Messages)
        for (u in users) localDataSource.saveUser(u)
        for (c in chatRooms) localDataSource.saveChatRoom(c)
        for (m in allMessages) localDataSource.saveChatMessage(m)

        var loaded = localDataSource.getChatMessages(chatRoom1.id)
        assertThat(loaded, `is`(chatRoom1Messages))
        loaded = localDataSource.getChatMessages(chatRoom2.id)
        assertThat(loaded, `is`(chatRoom2Messages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetChatMessageById() = runBlockingTest {
        val user = User("+12345556764")
        val chatRoom = ChatRoom("cr_1", user.phoneNumber)
        val messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom.id, "Hey"),
            ChatMessage(randomUUID().toString(), chatRoom.id, "Hello"),
        )
        localDataSource.saveUser(user)
        localDataSource.saveChatRoom(chatRoom)
        for (m in messages) localDataSource.saveChatMessage(m)

        val selectedMessage = messages[1]
        val loaded = localDataSource.getChatMessage(selectedMessage.id)
        assertThat(loaded, `is`(selectedMessage))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChats_ReturnsAggregationOfUsersChatRoomsAndMessages() = runBlockingTest {
        val chatRoom1User = User("+12345556732")
        val chatRoom2User = User("+12345556764")
        val users = listOf(chatRoom1User, chatRoom2User)

        val chatRoom1UserDetail = UserDetail(users[0].phoneNumber, "John", "Doe")
        val chatRoom2UserDetail = UserDetail(users[1].phoneNumber, "Jane", "Doe")
        val userDetails = listOf(chatRoom1UserDetail, chatRoom2UserDetail)

        val chatRoom1 = ChatRoom("cr_1", users[0].phoneNumber)
        val chatRoom2 = ChatRoom("cr_2", users[1].phoneNumber)
        val chatRooms = listOf(chatRoom1, chatRoom2)

        val time = System.currentTimeMillis()
        val chatRoom1Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hey", timestamp = time),
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hello", timestamp = time + 1)
        )
        val chatRoom2Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom2.id, "What's up", timestamp = time + 2),
            ChatMessage(randomUUID().toString(), chatRoom2.id, "Good", timestamp = time + 3)
        )
        val allMessages = mutableListOf<ChatMessage>()
        allMessages.addAll(chatRoom1Messages)
        allMessages.addAll(chatRoom2Messages)
        for (u in users) localDataSource.saveUser(u)
        for (d in userDetails) localDataSource.saveUserDetail(d)
        for (c in chatRooms) localDataSource.saveChatRoom(c)
        for (m in allMessages) localDataSource.saveChatMessage(m)

        val loaded = localDataSource.getChats()
        assertThat(loaded.size, `is`(2))

        val chat1 = loaded[1]
        assertThat(chat1.chatRoomId, `is`(chatRoom1.id))
        assertThat(chat1.photoUrl, `is`(chatRoom1UserDetail.photoUrl))
        assertThat(chat1.userName, `is`(chatRoom1UserDetail.firstName))
        assertThat(chat1.phoneNumber, `is`(chatRoom1User.phoneNumber))
        assertThat(chat1.recentMessage, `is`(chatRoom1Messages[1].message))
        assertThat(chat1.timestamp, `is`(chatRoom1Messages[1].timestamp))

        val chat2 = loaded[0]
        assertThat(chat2.chatRoomId, `is`(chatRoom2.id))
        assertThat(chat2.photoUrl, `is`(chatRoom2UserDetail.photoUrl))
        assertThat(chat2.userName, `is`(chatRoom2UserDetail.firstName))
        assertThat(chat2.phoneNumber, `is`(chatRoom2User.phoneNumber))
        assertThat(chat2.recentMessage, `is`(chatRoom2Messages[1].message))
        assertThat(chat2.timestamp, `is`(chatRoom2Messages[1].timestamp))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun savingSameUserDetailAgainUpdatesItsValue() = runBlockingTest {
        val user = User("+12345556732")
        val userDetail = UserDetail(user.phoneNumber, "John", "Doe")
        localDataSource.saveUser(user)
        localDataSource.saveUserDetail(userDetail)

        val updateUserDetail = userDetail.copy(firstName = "Jane")
        localDataSource.saveUserDetail(updateUserDetail)

        val loaded = localDataSource.getUserDetail(userDetail.phoneNumber)
        assertThat(loaded, `is`(not(userDetail)))
        assertThat(loaded, `is`(updateUserDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun savingSameChatRoomAgainIsIgnored() = runBlockingTest {
        val user1 = User("+12345556764")
        val chatRoom = ChatRoom("cr_2", user1.phoneNumber)
        localDataSource.saveUser(user1)
        localDataSource.saveChatRoom(chatRoom)

        val user2 = User("+12345556732")
        localDataSource.saveUser(user2)
        val updateChatRoom = chatRoom.copy(phoneNumber = user2.phoneNumber)
        localDataSource.saveChatRoom(updateChatRoom)

        val loaded = localDataSource.getChatRoom(chatRoom.id)
        assertThat(loaded, `is`(chatRoom))
        assertThat(loaded, `is`(not(updateChatRoom)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun savingSameChatMessageIsIgnored() = runBlockingTest {
        val user = User("+12345556764")
        val chatRoom = ChatRoom("cr_1", user.phoneNumber)
        val message = ChatMessage(randomUUID().toString(), chatRoom.id, "Hey")
        localDataSource.saveUser(user)
        localDataSource.saveChatRoom(chatRoom)
        localDataSource.saveChatMessage(message)

        val updatedChatMessage = message.copy(message = "Hello")
        localDataSource.saveChatMessage(updatedChatMessage)

        val loaded = localDataSource.getChatMessage(message.id)
        assertThat(loaded, `is`(message))
        assertThat(loaded, `is`(not(updatedChatMessage)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllInsertedUsersAndGetAll_ReturnsEmptyList() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764"),
            User("+193445556732")
        )
        for (u in users) localDataSource.saveUser(u)

        localDataSource.deleteAllUsers()

        val loaded = localDataSource.getUsers()
        assertThat(loaded, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserAndGetAllUsers_ReturnsListWithoutDeletedUser() = runBlockingTest {
        val user = User("+12345556732")
        localDataSource.saveUser(user)

        localDataSource.deleteUser(user)

        val loaded = localDataSource.getUsers()
        assertThat(loaded.contains(user), `is`(false))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserByPhoneNumberAndGetAllUsers_ReturnsListWithoutDeletedUser() = runBlockingTest {
        val user = User("+12345556732")
        localDataSource.saveUser(user)

        localDataSource.deleteUser(user.phoneNumber)

        val loaded = localDataSource.getUsers()
        assertThat(loaded.contains(user), `is`(false))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deletingUserUnregistersIt() = runBlockingTest {
        val user = User("+12345556732")
        val userDetail = UserDetail(user.phoneNumber, "John", "Doe")
        localDataSource.saveUser(user)
        localDataSource.saveUserDetail(userDetail)

        assertThat(localDataSource.isRegistered(user.phoneNumber), `is`(true))

        localDataSource.deleteUser(user.phoneNumber)
        assertThat(localDataSource.isRegistered(user.phoneNumber), `is`(false))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deletingAllUsersAlsoDeletesAllUserDetails() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val userDetails = listOf(
            UserDetail(users[0].phoneNumber, "John", "Doe"),
            UserDetail(users[1].phoneNumber, "Jane", "Doe")
        )
        for (u in users) localDataSource.saveUser(u)
        for (d in userDetails) localDataSource.saveUserDetail(d)

        localDataSource.deleteAllUsers()

        assertThat(localDataSource.getUsers(), `is`(emptyList()))
        assertThat(localDataSource.getUserDetails(), `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deletingUserAlsoDeletesItsDetail() = runBlockingTest {
        val user = User("+12345556732")
        val userDetail = UserDetail(user.phoneNumber, "John", "Doe")
        localDataSource.saveUser(user)
        localDataSource.saveUserDetail(userDetail)

        localDataSource.deleteUser(user)

        assertThat(localDataSource.getUserDetail(userDetail.phoneNumber), `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllInsertedChatsRoomsAndGetAll_ReturnsEmptyList() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val chatRooms = listOf(
            ChatRoom("cr_1", users[0].phoneNumber),
            ChatRoom("cr_2", users[1].phoneNumber)
        )
        for (u in users) localDataSource.saveUser(u)
        for (c in chatRooms) localDataSource.saveChatRoom(c)

        localDataSource.deleteAllChatRooms()

        val loaded = localDataSource.getChatRooms()
        assertThat(loaded, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoomAndGetById_ReturnsNull() = runBlockingTest {
        val user = User("+12345556764")
        val chatRoom = ChatRoom("cr_2", user.phoneNumber)
        localDataSource.saveUser(user)
        localDataSource.saveChatRoom(chatRoom)

        localDataSource.deleteChatRoom(chatRoom)

        val loaded = localDataSource.getChatRoom(chatRoom.id)
        assertThat(loaded, `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoomByIdAndGetById_ReturnsNull() = runBlockingTest {
        val user = User("+12345556764")
        val chatRoom = ChatRoom("cr_2", user.phoneNumber)
        localDataSource.saveUser(user)
        localDataSource.saveChatRoom(chatRoom)

        localDataSource.deleteChatRoom(chatRoom.id)

        val loaded = localDataSource.getChatRoom(chatRoom.id)
        assertThat(loaded, `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteInsertedChatMessagesAndGetAll_ReturnsEmptyList() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val chatRoom1 = ChatRoom("cr_1", users[0].phoneNumber)
        val chatRoom2 = ChatRoom("cr_2", users[1].phoneNumber)
        val chatRooms = listOf(chatRoom1, chatRoom2)
        val chatRoom1Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hey"),
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hello"),
        )
        val chatRoom2Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom2.id, "What's up"),
            ChatMessage(randomUUID().toString(), chatRoom2.id, "Good, what about you?"),
        )
        val allMessages = mutableListOf<ChatMessage>()
        allMessages.addAll(chatRoom1Messages)
        allMessages.addAll(chatRoom2Messages)
        for (u in users) localDataSource.saveUser(u)
        for (c in chatRooms) localDataSource.saveChatRoom(c)
        for (m in allMessages) localDataSource.saveChatMessage(m)

        localDataSource.deleteAllChatMessages()

        val loaded = localDataSource.getChatMessages()
        assertThat(loaded, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessageByChatRoomIdAndGetByChatRoomId_ReturnsEmptyList() = runBlockingTest {
        val users = listOf(
            User("+12345556732"),
            User("+12345556764")
        )
        val chatRoom1 = ChatRoom("cr_1", users[0].phoneNumber)
        val chatRoom2 = ChatRoom("cr_2", users[1].phoneNumber)
        val chatRooms = listOf(chatRoom1, chatRoom2)
        val chatRoom1Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hey"),
            ChatMessage(randomUUID().toString(), chatRoom1.id, "Hello"),
        )
        val chatRoom2Messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom2.id, "What's up"),
            ChatMessage(randomUUID().toString(), chatRoom2.id, "Good, what about you?"),
        )
        val allMessages = mutableListOf<ChatMessage>()
        allMessages.addAll(chatRoom1Messages)
        allMessages.addAll(chatRoom2Messages)
        for (u in users) localDataSource.saveUser(u)
        for (c in chatRooms) localDataSource.saveChatRoom(c)
        for (m in allMessages) localDataSource.saveChatMessage(m)

        localDataSource.deleteChatMessages(chatRoom1.id)

        // Only chat messages with given chatRoomId are deleted
        var loaded = localDataSource.getChatMessages(chatRoom1.id)
        assertThat(loaded, `is`(emptyList()))
        loaded = localDataSource.getChatMessages(chatRoom2.id)
        assertThat(loaded, `is`(chatRoom2Messages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessageAndGetById_ReturnsNullValue() = runBlockingTest {
        val user = User("+12345556764")
        val chatRoom = ChatRoom("cr_1", user.phoneNumber)
        val messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom.id, "Hey"),
            ChatMessage(randomUUID().toString(), chatRoom.id, "Hello"),
        )
        localDataSource.saveUser(user)
        localDataSource.saveChatRoom(chatRoom)
        for (m in messages) localDataSource.saveChatMessage(m)

        val selectedMessage = messages[1]
        localDataSource.deleteChatMessage(selectedMessage)

        val loaded = localDataSource.getChatMessage(selectedMessage.id)
        assertThat(loaded, `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessageByIdAndGetById_ReturnsNullValue() = runBlockingTest {
        val user = User("+12345556764")
        val chatRoom = ChatRoom("cr_1", user.phoneNumber)
        val messages = listOf(
            ChatMessage(randomUUID().toString(), chatRoom.id, "Hey"),
            ChatMessage(randomUUID().toString(), chatRoom.id, "Hello"),
        )
        localDataSource.saveUser(user)
        localDataSource.saveChatRoom(chatRoom)
        for (m in messages) localDataSource.saveChatMessage(m)

        val selectedMessage = messages[1]
        localDataSource.deleteChatMessage(selectedMessage.id)

        val loaded = localDataSource.getChatMessage(selectedMessage.id)
        assertThat(loaded, `is`(nullValue()))
    }
}