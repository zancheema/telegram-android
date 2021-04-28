package com.zancheema.android.telegram.data.source.remote

import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.FakeContentProvider
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.ChatRoom
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.data.source.remote.dto.ChatMessageDTO
import com.zancheema.android.telegram.data.source.remote.dto.ChatRoomDTO
import com.zancheema.android.telegram.data.source.remote.dto.asDomainModel
import com.zancheema.android.telegram.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

/**
 * WARNING: This test suite user communicate with real server
 *  and users real querying. It is created temporarily to verify
 *  [FirestoreDataSource] business logic
 */
class FirestoreDataSourceTest {
    // Stub Dependencies
    // Both phone numbers are fake and safe for testing
    // Because no real user data exists within these phoneNumber collections
    private val currentPhoneNumber = "+12345557654"
    private val otherPhoneNumber = "+14355556781"
    private lateinit var contentProvider: FakeContentProvider
    private lateinit var firestore: Firestore

    // Class Under Test
    private lateinit var firestoreDataSource: FirestoreDataSource

    @Before
    fun init() {
        FirebaseApp.initializeApp(
            ApplicationProvider.getApplicationContext()
        )

        contentProvider = FakeContentProvider()
        contentProvider.currentPhoneNumber = currentPhoneNumber
        firestore = Firestore(contentProvider)
        firestoreDataSource = FirestoreDataSource(firestore, Dispatchers.Main)
    }

    @Test
    fun saveAndGetUserDetailsByPhoneNumbersReturnsDetails_ThenDeleteAndGetDetailsReturnsEmptyList() =
        runBlocking {
            val userDetails = listOf(
                UserDetail(currentPhoneNumber, "John", "Doe"),
                UserDetail(otherPhoneNumber, "Jane", "Doe")
            )
            for (d in userDetails) firestoreDataSource.saveUserDetail(d)
            val phoneNumbers = userDetails.map { it.phoneNumber }

            var loaded = firestoreDataSource.getUserDetails(phoneNumbers)
            assertThat(loaded.succeeded, `is`(true))
            loaded as Success
            assertThat(loaded.data, `is`(userDetails))

            for (p in phoneNumbers) firestoreDataSource.deleteUser(p)
            loaded = firestoreDataSource.getUserDetails(phoneNumbers)
            assertThat(loaded.succeeded, `is`(true))
            loaded as Success
            assertThat(loaded.data, `is`(emptyList()))
        }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetUserDetailReturnsSavedDetail_ThenDeleteUserAndGetDetailReturnsError() =
        runBlocking {
            val userDetail = UserDetail(currentPhoneNumber, "John", "Doe")
            firestoreDataSource.saveUserDetail(userDetail)

            var loaded = firestoreDataSource.getUserDetail(currentPhoneNumber)
            assertThat(loaded.succeeded, `is`(true))
            loaded as Success
            assertThat(loaded.data, `is`(userDetail))

            firestoreDataSource.deleteUser(userDetail.phoneNumber)

            loaded = firestoreDataSource.getUserDetail(currentPhoneNumber)
            assertThat(loaded is Error, `is`(true))
        }

    @Test
    fun saveAndGetChatRoomsReturnsSavedRooms_ThenDeleteAllChatRoomsAndGetReturnsEmptyList() =
        runBlocking {
            val chatRooms = listOf(
                ChatRoom("cr_1", otherPhoneNumber),
                ChatRoom("cr_2", otherPhoneNumber)
            )
            for (c in chatRooms) firestoreDataSource.saveChatRoom(c)

            var loaded = firestoreDataSource.getChatRooms()
            assertThat(loaded.succeeded, `is`(true))
            loaded as Success
            assertThat(loaded.data, `is`(chatRooms))
            // Chat Rooms are also created in other user's database
            val otherChatRooms = getChatRooms(otherPhoneNumber)
            assertThat(
                otherChatRooms,
                `is`(chatRooms.map { room -> room.copy(phoneNumber = currentPhoneNumber) })
            )

            firestoreDataSource.deleteAllChatRooms()

            loaded = firestoreDataSource.getChatRooms()
            assertThat(loaded.succeeded, `is`(true))
            loaded as Success
            assertThat(loaded.data, `is`(emptyList()))
        }

    @Test
    fun saveAndGetChatRoomByIdReturnsSavedRoom_ThenDeleteChatRoomByIdAndGetByIdReturnsError() =
        runBlocking {
            val chatRoom = ChatRoom("cr", currentPhoneNumber)
            firestoreDataSource.saveChatRoom(chatRoom)

            var loaded = firestoreDataSource.getChatRoom(chatRoom.id)
            assertThat(loaded.succeeded, `is`(true))
            assertThat((loaded as Success).data, `is`(chatRoom))

            firestoreDataSource.deleteChatRoom(chatRoom)

            loaded = firestoreDataSource.getChatRoom(chatRoom.id)
            assertThat(loaded is Error, `is`(true))
        }

    @Test
    fun saveAndGetChatMessagesByChatRoomIdReturnsSavedMessages_ThenDeleteByChatRoomIdAndGetReturnsEmptyList() =
        runBlocking {
            val chatRoom1 = ChatRoom("cr_1", otherPhoneNumber)
            val chatRoom2 = ChatRoom("cr_2", otherPhoneNumber)
            firestoreDataSource.saveChatRoom(chatRoom1)
            firestoreDataSource.saveChatRoom(chatRoom2)

            val chatRoom1Messages = listOf(
                ChatMessage("cm_1", chatRoom1.id, "Hey"),
                ChatMessage("cm_2", chatRoom1.id, "Hello")
            )
            val chatRoom2Messages = listOf(
                ChatMessage("cm_3", chatRoom2.id, "Hello there")
            )
            val allChatMessages = mutableListOf<ChatMessage>().apply {
                addAll(chatRoom1Messages)
                addAll(chatRoom2Messages)
            }
            for (m in allChatMessages) firestoreDataSource.saveChatMessage(m)

            var loaded = firestoreDataSource.getChatMessages(chatRoom1.id)
            assertThat(loaded.succeeded, `is`(true))
            assertThat((loaded as Success).data, `is`(chatRoom1Messages))
            // ChatMessages are also stored in otherUsers database
            val otherUserMessages = getChatMessages(chatRoom2.id, chatRoom2.phoneNumber)
            assertThat(otherUserMessages, `is`(chatRoom2Messages.map { it.copy(isMine = false) }))

            firestoreDataSource.deleteChatMessages(chatRoom1.id)

            loaded = firestoreDataSource.getChatMessages(chatRoom1.id)
            assertThat(loaded.succeeded, `is`(true))
            assertThat((loaded as Success).data, `is`(emptyList()))

            firestoreDataSource.deleteChatMessages(chatRoom2.id)
        }

    private suspend fun getChatRooms(phoneNumber: String): List<ChatRoom> {
        return firestore.chatRoomsCollection(phoneNumber)
            .get()
            .await()
            .toObjects(ChatRoomDTO::class.java)
            .map { it.asDomainModel() }
    }

    private suspend fun getChatMessages(
        chatRoomId: String,
        phoneNumber: String
    ): List<ChatMessage> {
        return firestore.chatMessagesCollection(chatRoomId, phoneNumber)
            .get()
            .await()
            .toObjects(ChatMessageDTO::class.java)
            .map { it.asDomainModel() }
    }
}