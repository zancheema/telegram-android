package com.zancheema.android.telegram.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.ChatRoom
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultRepositoryTest {

    // Dependencies
    private lateinit var localDataSource: FakeDataSource
    private lateinit var remoteDataSource: FakeDataSource

    // Class Under Test
    private lateinit var repository: DefaultRepository

    // Fake Data (user1 and user2 exist both locally and remotely with different details)
    private val localUser1 = User("+178555467")
    private val localUser2 = User("+278555467")
    private val localUsers = listOf(localUser1, localUser2)

    private val remoteUser1 = User(localUser1.phoneNumber)
    private val remoteUser2 = User(localUser2.phoneNumber)
    private val remoteUser3 = User("+478555467")
    private val remoteUsers = listOf(remoteUser1, remoteUser2, remoteUser3)

    private val localUserDetail1 = UserDetail(localUser1.phoneNumber, "John", "Doe")
    private val localUserDetail2 = UserDetail(localUser2.phoneNumber, "Jane", "Doe")
    private val localUserDetails = listOf(localUserDetail1, localUserDetail2)

    private val remoteUserDetail1 = UserDetail(remoteUser1.phoneNumber, "Mike", "Doe")
    private val remoteUserDetail2 = UserDetail(remoteUser2.phoneNumber, "Mike", "Joe")
    private val remoteUserDetail3 = UserDetail(remoteUser3.phoneNumber, "Julian", "Joe")
    private val remoteUserDetails = listOf(remoteUserDetail1, remoteUserDetail2, remoteUserDetail3)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun initRepository() = runBlockingTest {
        localDataSource = FakeDataSource()
        remoteDataSource = FakeDataSource()

        localDataSource.apply {
            localUsers.forEach { saveUser(it) }
            localUserDetails.forEach { saveUserDetail(it) }
        }
        remoteDataSource.apply {
            remoteUsers.forEach { saveUser(it) }
            remoteUserDetails.forEach { saveUserDetail(it) }
        }

        repository = DefaultRepository(remoteDataSource, localDataSource, Dispatchers.Main)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUser_GetsUserFromLocalDataSource() = runBlockingTest {
        val users = repository.getUsers() as Success
        assertThat(users.data, `is`(localUsers))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetails_GetsUsersDetailsFromLocalDataSource() = runBlockingTest {
        val details = repository.getUserDetails() as Success
        assertThat(details.data, `is`(localUserDetails))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshUserDetailSetsLocalUserDetailsAsRemoteForLocalUsers() = runBlockingTest {
        repository.refreshUserDetails()

        val details = repository.getUserDetails() as Success
        assertThat(details.data, `is`(remoteUserDetails.subList(0, 2)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetailsWithForceUpdate_GetsRemoteUserDetailsForLocalUsers() = runBlockingTest {
        val details = repository.getUserDetails(true)

        assertThat((details as Success).data, `is`(remoteUserDetails.subList(0, 2)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetailByPhoneNumbersReturnsDetailsFromLocalDataSource() = runBlockingTest {
        val userDetails = listOf(localUserDetail1, localUserDetail2)
        val loaded = repository.getUserDetails(userDetails.map { it.phoneNumber }) as Success
        assertThat(loaded.data, `is`(userDetails))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshUserDetailsByPhoneSetsLocalDetailsFromRemote() = runBlockingTest {
        val userDetails = listOf(localUserDetail1, localUserDetail2)
        val phoneNumbers = userDetails.map { it.phoneNumber }

        repository.refreshUserDetails(phoneNumbers)

        val loaded = repository.getUserDetails(phoneNumbers) as Success
        assertThat(loaded.data, `is`(listOf(remoteUserDetail1, remoteUserDetail2)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetailsByPhoneNumberWithForceUpdateGetsDetailsFromRemoteDataSource() =
        runBlockingTest {
            val userDetails = listOf(localUserDetail1, localUserDetail2)
            val phoneNumbers = userDetails.map { it.phoneNumber }

            val loaded = repository.getUserDetails(phoneNumbers, true) as Success
            assertThat(loaded.data, `is`(listOf(remoteUserDetail1, remoteUserDetail2)))
        }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetailReturnsDetailFromLocalUserDetail() = runBlockingTest {
        val userDetail = repository.getUserDetail(localUser1.phoneNumber) as Success
        assertThat(userDetail.data, `is`(localUserDetail1))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshUserDetailSetsLocalUserDetailFromRemote() = runBlockingTest {
        repository.refreshUserDetail(localUser1.phoneNumber)

        val userDetail = repository.getUserDetail(localUser1.phoneNumber) as Success
        assertThat(userDetail.data, `is`(remoteUserDetail1))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetailWithForceUpdateReturnsDetailFromRemoteDataSource() = runBlockingTest {
        val userDetail = repository.getUserDetail(localUser1.phoneNumber, true) as Success

        assertThat(userDetail.data, `is`(remoteUserDetail1))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun isRegisteredReturnsWhetherUserIsRegisteredLocally() = runBlockingTest {
        val newUserDetail = UserDetail("+547865554321", "First", "Second")
        remoteDataSource.saveUserDetail(newUserDetail)

        val isRegistered = repository.isRegistered(newUserDetail.phoneNumber) as Success
        assertThat(isRegistered.data, `is`(false))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun isRegisteredWithForceUpdateReturnsWhetherUserIsRegisteredRemotely() = runBlockingTest {
        val newUserDetail = UserDetail("+547865554321", "First", "Second")
        remoteDataSource.saveUserDetail(newUserDetail)

        val isRegistered = repository.isRegistered(newUserDetail.phoneNumber, true) as Success
        assertThat(isRegistered.data, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatRoomsGetsChatRoomsFromLocalDataSource() = runBlockingTest {
        val chatRooms = listOf(
            ChatRoom("cr_1", remoteUser1.phoneNumber),
            ChatRoom("cr_2", remoteUser2.phoneNumber)
        )
        localDataSource.setChatRooms(chatRooms)

        val loaded = repository.getChatRooms() as Success
        assertThat(loaded.data, `is`(chatRooms))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshChatRoomsSetsLocalChatRoomsAsRemoteChatRooms() = runBlockingTest {
        val chatRooms = listOf(
            ChatRoom("cr_1", remoteUser1.phoneNumber),
            ChatRoom("cr_2", remoteUser2.phoneNumber)
        )
        remoteDataSource.setChatRooms(chatRooms)

        repository.refreshChatRooms()

        val loaded = localDataSource.getChatRooms() as Success
        assertThat(loaded.data, `is`(chatRooms))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatRoomsWithForceUpdateGetsChatRoomsFromRemoteDataSource() = runBlockingTest {
        val chatRooms = listOf(
            ChatRoom("cr_1", remoteUser1.phoneNumber),
            ChatRoom("cr_2", remoteUser2.phoneNumber)
        )
        remoteDataSource.setChatRooms(chatRooms)

        val loaded = repository.getChatRooms(true) as Success
        assertThat(loaded.data, `is`(chatRooms))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatRoomGetsChatRoomFromLocalDataSource() = runBlockingTest {
        val localRoom = ChatRoom("room_1", localUser2.phoneNumber)
        localDataSource.saveChatRoom(localRoom)
        val remoteRoom = ChatRoom(localRoom.id, localUser1.phoneNumber)
        remoteDataSource.saveChatRoom(remoteRoom)

        val loaded = repository.getChatRoom(localRoom.id) as Success
        assertThat(loaded.data, `is`(localRoom))
        assertThat(loaded.data, `is`(not(remoteRoom)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshChatRoomReplacesLocalDataSourceChatRoomWithRemote() = runBlockingTest {
        val localRoom = ChatRoom("room_1", localUser2.phoneNumber)
        localDataSource.saveChatRoom(localRoom)
        val remoteRoom = ChatRoom(localRoom.id, localUser1.phoneNumber)
        remoteDataSource.saveChatRoom(remoteRoom)

        repository.refreshChatRoom(localRoom.id)

        val loaded = localDataSource.getChatRoom(localRoom.id) as Success
        assertThat(loaded.data, `is`(remoteRoom))
        assertThat(loaded.data, `is`(not(localRoom)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatRoomWithForceUpdateGetsChatRoomFromRemoteDataSource() = runBlockingTest {
        val localRoom = ChatRoom("room_1", localUser2.phoneNumber)
        localDataSource.saveChatRoom(localRoom)
        val remoteRoom = ChatRoom(localRoom.id, localUser1.phoneNumber)
        remoteDataSource.saveChatRoom(remoteRoom)

        val loaded = repository.getChatRoom(localRoom.id, true) as Success
        assertThat(loaded.data, `is`(remoteRoom))
        assertThat(loaded.data, `is`(not(localRoom)))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatMessagesGetsMessagesFromLocalDataSource() = runBlockingTest {
        val chatMessages = listOf(
            ChatMessage("m1", "cr", "Hey"),
            ChatMessage("m2", "cr", "Hi")
        )
        localDataSource.setChatMessages(chatMessages)

        val loaded = repository.getChatMessages() as Success
        assertThat(loaded.data, `is`(chatMessages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshChatMessagesSetsLocalDataSourceChatMessagesAsRemote() = runBlockingTest {
        val chatMessages = listOf(
            ChatMessage("m1", "cr", "Hey"),
            ChatMessage("m2", "cr", "Hi")
        )
        remoteDataSource.setChatMessages(chatMessages)

        repository.refreshChatMessages()

        val loaded = localDataSource.getChatMessages() as Success
        assertThat(loaded.data, `is`(chatMessages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatMessagesWithForceUpdateGetsChatMessagesFromRemoteDataSource() = runBlockingTest {
        val chatRoom = ChatRoom("cr", localUser1.phoneNumber)
        val chatMessages = listOf(
            ChatMessage("m1", chatRoom.id, "Hey"),
            ChatMessage("m2", chatRoom.id, "Hi")
        )
        localDataSource.setChatMessages(chatMessages)

        val loaded = repository.getChatMessages(chatRoom.id) as Success
        assertThat(loaded.data, `is`(chatMessages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatMessagesByChatRoomIdReturnsChatMessagesFromLocalDataSource() = runBlockingTest {
        val chatRoom1 = ChatRoom("cr_1", localUser1.phoneNumber)
        val chatRoom2 = ChatRoom("cr_2", remoteUser2.phoneNumber)
        val chatRoom1LocalMessages = listOf(
            ChatMessage("m1", chatRoom1.id, "Hey"),
            ChatMessage("m2", chatRoom1.id, "Hi")
        )
        val chatRoom2LocalMessages = listOf(
            ChatMessage("m3", chatRoom2.id, "Hey")
        )
        val chatRoom1RemoteMessages = listOf(
            ChatMessage("m4", chatRoom1.id, "Hey there")
        )
        val chatRoom2RemoteMessages = listOf(
            ChatMessage("m5", chatRoom2.id, "How are you doing?")
        )
        for (m in chatRoom1LocalMessages) localDataSource.saveChatMessage(m)
        for (m in chatRoom2LocalMessages) localDataSource.saveChatMessage(m)
        for (m in chatRoom1RemoteMessages) remoteDataSource.saveChatMessage(m)
        for (m in chatRoom2RemoteMessages) remoteDataSource.saveChatMessage(m)

        val loaded = repository.getChatMessages(chatRoom1.id) as Success
        assertThat(loaded.data, `is`(chatRoom1LocalMessages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshChatMessagesByChatRoomIdSetsLocalChatRoomMessagesFromRemote() = runBlockingTest {
        val chatRoom1 = ChatRoom("cr_1", localUser1.phoneNumber)
        val chatRoom2 = ChatRoom("cr_2", remoteUser2.phoneNumber)
        val chatRoom1LocalMessages = listOf(
            ChatMessage("m1", chatRoom1.id, "Hey"),
            ChatMessage("m2", chatRoom1.id, "Hi")
        )
        val chatRoom2LocalMessages = listOf(
            ChatMessage("m3", chatRoom2.id, "Hey")
        )
        val chatRoom1RemoteMessages = listOf(
            ChatMessage("m4", chatRoom1.id, "Hey there")
        )
        val chatRoom2RemoteMessages = listOf(
            ChatMessage("m5", chatRoom2.id, "How are you doing?")
        )
        for (m in chatRoom1LocalMessages) localDataSource.saveChatMessage(m)
        for (m in chatRoom2LocalMessages) localDataSource.saveChatMessage(m)
        for (m in chatRoom1RemoteMessages) remoteDataSource.saveChatMessage(m)
        for (m in chatRoom2RemoteMessages) remoteDataSource.saveChatMessage(m)

        repository.refreshChatMessages(chatRoom1.id)

        val loaded = repository.getChatMessages(chatRoom1.id) as Success
        assertThat(loaded.data, `is`(chatRoom1RemoteMessages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatRoomMessagesByChatRoomIdWithForceUpdateReturnsChatChatMessagesFromRemoteDataSource() =
        runBlockingTest {
            val chatRoom1 = ChatRoom("cr_1", localUser1.phoneNumber)
            val chatRoom2 = ChatRoom("cr_2", remoteUser2.phoneNumber)
            val chatRoom1LocalMessages = listOf(
                ChatMessage("m1", chatRoom1.id, "Hey"),
                ChatMessage("m2", chatRoom1.id, "Hi")
            )
            val chatRoom2LocalMessages = listOf(
                ChatMessage("m3", chatRoom2.id, "Hey")
            )
            val chatRoom1RemoteMessages = listOf(
                ChatMessage("m4", chatRoom1.id, "Hey there")
            )
            val chatRoom2RemoteMessages = listOf(
                ChatMessage("m5", chatRoom2.id, "How are you doing?")
            )
            for (m in chatRoom1LocalMessages) localDataSource.saveChatMessage(m)
            for (m in chatRoom2LocalMessages) localDataSource.saveChatMessage(m)
            for (m in chatRoom1RemoteMessages) remoteDataSource.saveChatMessage(m)
            for (m in chatRoom2RemoteMessages) remoteDataSource.saveChatMessage(m)

            val loaded = repository.getChatMessages(chatRoom1.id, true) as Success
            assertThat(loaded.data, `is`(chatRoom1RemoteMessages))
        }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatMessageReturnsMessageFromLocalDataSource() = runBlockingTest {
        val localMessage = ChatMessage("cm", "cr", "Hey there")
        val remoteMessage = localMessage.copy(message = "Hi")
        localDataSource.saveChatMessage(localMessage)
        remoteDataSource.saveChatMessage(remoteMessage)

        val loaded = repository.getChatMessage(localMessage.id) as Success
        assertThat(loaded.data, `is`(localMessage))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun refreshChatMessageReplacesLocalChatMessageByRemote() = runBlockingTest {
        val localMessage = ChatMessage("cm", "cr", "Hey there")
        val remoteMessage = localMessage.copy(message = "Hi")
        localDataSource.saveChatMessage(localMessage)
        remoteDataSource.saveChatMessage(remoteMessage)

        repository.refreshChatMessage(localMessage.id)

        val loaded = repository.getChatMessage(localMessage.id) as Success
        assertThat(loaded.data, `is`(remoteMessage))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getChatMessageWithForceUpdateGetsChatMessageFromRemoteDataSource() = runBlockingTest {
        val localMessage = ChatMessage("cm", "cr", "Hey there")
        val remoteMessage = localMessage.copy(message = "Hi")
        localDataSource.saveChatMessage(localMessage)
        remoteDataSource.saveChatMessage(remoteMessage)

        val loaded = repository.getChatMessage(localMessage.id, true) as Success
        assertThat(loaded.data, `is`(remoteMessage))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun savingUserSuccessfullyReturnsResultAsTrue() = runBlockingTest {
        val user = User("+349805558970")

        val result = repository.saveUser(user) as Success
        assertThat(result.data, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserFailedReturnsError() = runBlockingTest {
        localDataSource.returnError = true
        val user = User("+349805558970")
        val result = repository.saveUser(user)

        assertThat(result is Error, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserSavesUserOnlyInLocalDataSource() = runBlockingTest {
        val user = User("+349805558970")
        repository.saveUser(user)

        // User is not saved remotely
        val remoteUsers = remoteDataSource.getUsers() as Success
        assertThat(remoteUsers.data.contains(user), `is`(false))
        // User is only saved locally
        val localUsers = localDataSource.getUsers() as Success
        assertThat(localUsers.data.contains(user), `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserDetailSuccessfullyReturnsResultAsTrue() = runBlockingTest {
        val user = User("+349805558970")
        val userDetail = UserDetail(user.phoneNumber, "Mike", "Jira")
        repository.saveUser(user)

        val result = repository.saveUserDetail(userDetail) as Success
        assertThat(result.data, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserDetailFailedReturnsError() = runBlockingTest {
        remoteDataSource.returnError = true
        val user = User("+349805558970")
        val userDetail = UserDetail(user.phoneNumber, "Mike", "Jira")
        repository.saveUser(user)
        val result = repository.saveUserDetail(userDetail)

        assertThat(result is Error, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserDetailSavesTheDetailBothRemotelyAndLocally() = runBlockingTest {
        val user = User("+349805558970")
        val userDetail = UserDetail(user.phoneNumber, "Mike", "Jira")
        repository.saveUser(user)
        repository.saveUserDetail(userDetail)


        // UserDetail is saved remotely
        val remoteDetail = remoteDataSource.getUserDetail(userDetail.phoneNumber) as Success
        assertThat(remoteDetail.data, `is`(userDetail))
        // User Detail is also saved locally
        val localDetail = localDataSource.getUserDetail(userDetail.phoneNumber) as Success
        assertThat(localDetail.data, `is`(userDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveChatRoomSavesChatRoomInRemoteAndLocalDataSource() = runBlockingTest {
        val chatRoom = ChatRoom("chat_room", remoteUser2.phoneNumber)
        repository.saveChatRoom(chatRoom)

        val remoteRoom = remoteDataSource.getChatRoom(chatRoom.id) as Success
        assertThat(remoteRoom.data, `is`(chatRoom))
        val localRoom = localDataSource.getChatRoom(chatRoom.id) as Success
        assertThat(localRoom.data, `is`(chatRoom))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveChatMessageSavesMessageInRemoteAndLocalDataSource() = runBlockingTest {
        val chatMessage = ChatMessage("cm", "cr", "Hey")
        repository.saveChatMessage(chatMessage)

        val remoteMessage = remoteDataSource.getChatMessage(chatMessage.id) as Success
        assertThat(remoteMessage.data, `is`(chatMessage))
        val localMessage = localDataSource.getChatMessage(chatMessage.id) as Success
        assertThat(localMessage.data, `is`(chatMessage))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUsersDeletesUsersFromRemoteAndLocalDataSource() = runBlockingTest {
        repository.deleteAllUsers()

        // Users deleted from RemoteDataSource
        val remoteUsers = remoteDataSource.getUsers() as Success
        assertThat(remoteUsers.data, `is`(emptyList()))
        // Users deleted from LocalDataSource
        val localUsers = localDataSource.getUsers() as Success
        assertThat(localUsers.data, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserDeletesUserFromRemoteAndLocalDataSource() = runBlockingTest {
        val user = localUser1
        repository.deleteUser(user.phoneNumber)

        val remoteUsers = remoteDataSource.getUsers() as Success
        assertThat(remoteUsers.data.contains(user), `is`(false))
        val localUser = localDataSource.getUsers() as Success
        assertThat(localUser.data.contains(user), `is`(false))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoomsDeletesChatRoomsFromRemoteAndLocalDataSource() = runBlockingTest {
        val chatRooms = listOf(
            ChatRoom("cr_1", localUser1.phoneNumber),
            ChatRoom("cr_2", localUser2.phoneNumber)
        )
        remoteDataSource.setChatRooms(chatRooms)
        localDataSource.setChatRooms(chatRooms)

        repository.deleteAllChatRooms()

        // Chat Rooms are delete remotely
        val remoteRooms = remoteDataSource.getChatRooms() as Success
        assertThat(remoteRooms.data, `is`(emptyList()))
        // Chat Rooms are delete locally
        val localRooms = localDataSource.getChatRooms() as Success
        assertThat(localRooms.data, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoomDeletesItFromRemoteAndLocalDataSource() = runBlockingTest {
        val chatRoom = ChatRoom("cr", localUser1.phoneNumber)
        remoteDataSource.saveChatRoom(chatRoom)
        localDataSource.saveChatRoom(chatRoom)

        repository.deleteChatRoom(chatRoom)

        val remoteRoom = remoteDataSource.getChatRoom(chatRoom.id)
        assertThat(remoteRoom is Error, `is`(true))
        val localRoom = remoteDataSource.getChatRoom(chatRoom.id)
        assertThat(localRoom is Error, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoomByIdDeletesItFromRemoteAndLocalDataSource() = runBlockingTest {
        val chatRoom = ChatRoom("cr", localUser1.phoneNumber)
        remoteDataSource.saveChatRoom(chatRoom)
        localDataSource.saveChatRoom(chatRoom)

        repository.deleteChatRoom(chatRoom.id)

        val remoteRoom = remoteDataSource.getChatRoom(chatRoom.id)
        assertThat(remoteRoom is Error, `is`(true))
        val localRoom = remoteDataSource.getChatRoom(chatRoom.id)
        assertThat(localRoom is Error, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessagesDeletesChatMessagesFromRemoteAndLocalDataSource() = runBlockingTest {
        val chatMessages = listOf(
            ChatMessage("m1", "cr", "Hey"),
            ChatMessage("m2", "cr", "Hi")
        )
        remoteDataSource.setChatMessages(chatMessages)
        localDataSource.setChatMessages(chatMessages)

        repository.deleteAllChatMessages()

        val remoteMessages = remoteDataSource.getChatMessages() as Success
        assertThat(remoteMessages.data, `is`(emptyList()))
        val localMessages = localDataSource.getChatMessages() as Success
        assertThat(localMessages.data, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessagesByChatRoomIdDeletesThemFromRemoteAndLocalDataSource() = runBlockingTest {
        val chatRoom1 = ChatRoom("cr_1", localUser1.phoneNumber)
        val chatRoom2 = ChatRoom("cr_2", remoteUser2.phoneNumber)
        val chatRoom1Messages = listOf(
            ChatMessage("m1", chatRoom1.id, "Hey"),
            ChatMessage("m2", chatRoom1.id, "Hi")
        )
        val chatRoom2Messages = listOf(
            ChatMessage("m3", chatRoom2.id, "Hey")
        )
        chatRoom1Messages.forEach { message ->
            remoteDataSource.saveChatMessage(message)
            localDataSource.saveChatMessage(message)
        }
        chatRoom2Messages.forEach { message ->
            remoteDataSource.saveChatMessage(message)
            localDataSource.saveChatMessage(message)
        }

        repository.deleteChatMessages(chatRoom1.id)

        val remoteMessages = remoteDataSource.getChatMessages(chatRoom1.id) as Success
        assertThat(remoteMessages.data, `is`(emptyList()))
        val localMessages = localDataSource.getChatMessages(chatRoom1.id) as Success
        assertThat(localMessages.data, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessageDeletesItFromRemoteAndLocalDataSource() = runBlockingTest {
        val chatMessage = ChatMessage("cm", localUser1.phoneNumber, "Hey")
        remoteDataSource.saveChatMessage(chatMessage)
        localDataSource.saveChatMessage(chatMessage)

        repository.deleteChatMessage(chatMessage)

        val localMessage = localDataSource.getChatMessage(chatMessage.id)
        assertThat(localMessage is Error, `is`(true))
        val remoteMessage = localDataSource.getChatMessage(chatMessage.id)
        assertThat(remoteMessage is Error, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessageByIdDeletesItFromRemoteAndLocalDataSource() = runBlockingTest {
        val chatMessage = ChatMessage("cm", localUser1.phoneNumber, "Hey")
        remoteDataSource.saveChatMessage(chatMessage)
        localDataSource.saveChatMessage(chatMessage)

        repository.deleteChatMessage(chatMessage.id)

        val localMessage = localDataSource.getChatMessage(chatMessage.id)
        assertThat(localMessage is Error, `is`(true))
        val remoteMessage = localDataSource.getChatMessage(chatMessage.id)
        assertThat(remoteMessage is Error, `is`(true))
    }
}