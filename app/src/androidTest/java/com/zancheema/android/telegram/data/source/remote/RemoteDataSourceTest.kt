package com.zancheema.android.telegram.data.source.remote

import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.FakeContentProvider
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.ChatRoom
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

/**
 * WARNING: This test suite user communicate with real server
 *  and users real querying. It is created temporarily to verify
 *  [RemoteDataSource] business logic
 */
class RemoteDataSourceTest {
    // Stub Dependencies
    // Both phone numbers are fake and safe for testing
    // Because no real user data exists within these phoneNumber collections
    private val currentPhoneNumber = "+12345557654"
    private val otherPhoneNumber = "+14355556781"
    private lateinit var contentProvider: FakeContentProvider
    private lateinit var firestore: Firestore

    // Class Under Test
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun init() {
        FirebaseApp.initializeApp(
            ApplicationProvider.getApplicationContext()
        )

        contentProvider = FakeContentProvider()
        contentProvider.currentPhoneNumber = currentPhoneNumber
        firestore = Firestore(contentProvider)
        remoteDataSource = RemoteDataSource(firestore, Dispatchers.Main)
    }

    @Test
    fun saveAndGetUserDetailsByPhoneNumbersReturnsDetails_ThenDeleteAndGetDetailsReturnsEmptyList() {
        val userDetails = listOf(
            UserDetail(currentPhoneNumber, "John", "Doe"),
            UserDetail(otherPhoneNumber, "Jane", "Doe")
        )
        for (d in userDetails) remoteDataSource.saveUserDetailBlocking(d)
        val phoneNumbers = userDetails.map { it.phoneNumber }

        var loaded = remoteDataSource.getUserDetailsBlocking(phoneNumbers)
        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data, `is`(userDetails))

        for (p in phoneNumbers) remoteDataSource.deleteUserBlocking(p)
        loaded = remoteDataSource.getUserDetailsBlocking(phoneNumbers)
        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetUserDetailReturnsSavedDetail_ThenDeleteUserAndGetDetailReturnsError() {
        val userDetail = UserDetail(currentPhoneNumber, "John", "Doe")
        remoteDataSource.saveUserDetailBlocking(userDetail)

        var loaded = remoteDataSource.getUserDetailBlocking(currentPhoneNumber)
        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data, `is`(userDetail))

        remoteDataSource.deleteUserBlocking(userDetail.phoneNumber)

        loaded = remoteDataSource.getUserDetailBlocking(currentPhoneNumber)
        assertThat(loaded is Error, `is`(true))
    }

    @Test
    fun saveAndGetChatRoomsReturnsSavedRooms_ThenDeleteAllChatRoomsAndGetReturnsEmptyList() {
        val chatRooms = listOf(
            ChatRoom("cr_1", currentPhoneNumber),
            ChatRoom("cr_2", otherPhoneNumber)
        )
        for (c in chatRooms) remoteDataSource.saveChatRoomBlocking(c)

        var loaded = remoteDataSource.getChatRoomsBlocking()
        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data, `is`(chatRooms))

        remoteDataSource.deleteAllChatRoomsBlocking()

        loaded = remoteDataSource.getChatRoomsBlocking()
        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data, `is`(emptyList()))
    }

    @Test
    fun saveAndGetChatRoomByIdReturnsSavedRoom_ThenDeleteChatRoomByIdAndGetByIdReturnsError() {
        val chatRoom = ChatRoom("cr", currentPhoneNumber)
        remoteDataSource.saveChatRoomBlocking(chatRoom)

        var loaded = remoteDataSource.getChatRoomBlocking(chatRoom.id)
        assertThat(loaded.succeeded, `is`(true))
        assertThat((loaded as Success).data, `is`(chatRoom))

        remoteDataSource.deleteChatRoomBlocking(chatRoom.id)

        loaded = remoteDataSource.getChatRoomBlocking(chatRoom.id)
        assertThat(loaded is Error, `is`(true))
    }

    @Test
    fun saveAndGetChatMessagesByChatRoomIdReturnsSavedMessages_ThenDeleteByChatRoomIdAndGetReturnsEmptyList() {
        val chatRoom1 = ChatRoom("cr_1", currentPhoneNumber)
        val chatRoom2 = ChatRoom("cr_2", otherPhoneNumber)
        val chatRoom1Messages = listOf(
            ChatMessage("cm_1", chatRoom1.id, "Hey"),
            ChatMessage("cm_2", chatRoom1.id, "Hello", false)
        )
        val chatRoom2Messages = listOf(
            ChatMessage("cm_3", chatRoom2.id, "Hello there")
        )
        val allChatMessages = mutableListOf<ChatMessage>().apply {
            addAll(chatRoom1Messages)
            addAll(chatRoom2Messages)
        }
        for (m in allChatMessages) remoteDataSource.saveChatMessageBlocking(m)

        var loaded = remoteDataSource.getChatMessagesBlocking(chatRoom1.id)
        assertThat(loaded.succeeded, `is`(true))
        assertThat((loaded as Success).data, `is`(chatRoom1Messages))

        remoteDataSource.deleteChatMessagesBlocking(chatRoom1.id)

        loaded = remoteDataSource.getChatMessagesBlocking(chatRoom1.id)
        assertThat(loaded.succeeded, `is`(true))
        assertThat((loaded as Success).data, `is`(emptyList()))

        remoteDataSource.deleteChatMessagesBlocking(chatRoom2.id)
    }

    private fun RemoteDataSource.getUserDetailsBlocking(phoneNumbers: List<String>): Result<List<UserDetail>> =
        runBlocking {
            return@runBlocking this@getUserDetailsBlocking.getUserDetails(phoneNumbers)
        }

    private fun RemoteDataSource.getUserDetailBlocking(phoneNumber: String): Result<UserDetail> =
        runBlocking {
            return@runBlocking this@getUserDetailBlocking.getUserDetail(phoneNumber)
        }

    private fun RemoteDataSource.getChatRoomsBlocking(): Result<List<ChatRoom>> = runBlocking {
        return@runBlocking this@getChatRoomsBlocking.getChatRooms()
    }

    private fun RemoteDataSource.getChatRoomBlocking(id: String): Result<ChatRoom> = runBlocking {
        return@runBlocking this@getChatRoomBlocking.getChatRoom(id)
    }

    private fun RemoteDataSource.getChatMessagesBlocking(chatRoomId: String): Result<List<ChatMessage>> =
        runBlocking {
            return@runBlocking this@getChatMessagesBlocking.getChatMessages(chatRoomId)
        }

    private fun RemoteDataSource.saveUserDetailBlocking(detail: UserDetail) = runBlocking {
        this@saveUserDetailBlocking.saveUserDetail(detail)
    }

    private fun RemoteDataSource.saveChatRoomBlocking(room: ChatRoom) = runBlocking {
        this@saveChatRoomBlocking.saveChatRoom(room)
    }

    private fun RemoteDataSource.saveChatMessageBlocking(message: ChatMessage) = runBlocking {
        this@saveChatMessageBlocking.saveChatMessage(message)
    }

    private fun RemoteDataSource.deleteUserBlocking(phoneNumber: String) = runBlocking {
        this@deleteUserBlocking.deleteUser(phoneNumber)
    }

    private fun RemoteDataSource.deleteAllChatRoomsBlocking() = runBlocking {
        this@deleteAllChatRoomsBlocking.deleteAllChatRooms()
    }

    private fun RemoteDataSource.deleteChatRoomBlocking(id: String) = runBlocking {
        this@deleteChatRoomBlocking.deleteChatRoom(id)
    }

    private fun RemoteDataSource.deleteChatMessagesBlocking(chatRoomId: String) = runBlocking {
        this@deleteChatMessagesBlocking.deleteChatMessages(chatRoomId)
    }
}