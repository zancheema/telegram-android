package com.zancheema.android.telegram.data.sync

import android.util.Log
import com.zancheema.android.telegram.data.source.remote.Firestore
import com.zancheema.android.telegram.data.source.remote.dto.ChatMessageDTO
import com.zancheema.android.telegram.data.source.remote.dto.ChatRoomDTO
import com.zancheema.android.telegram.data.source.remote.dto.UserDTO
import javax.inject.Inject

private const val TAG = "FirestoreDataObserver"

class FirestoreDataObserver @Inject constructor(
    private val firestore: Firestore
) : RemoteDataObserver {

    override fun observeUsers(onChange: (List<UserDTO>) -> Unit) {
        firestore.usersCollection().addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "usersCollectionSnapshot: ", error)
                return@addSnapshotListener
            }
            value?.let {
                val users = mutableListOf<UserDTO>()
                for (doc in it.documents) {
                    doc.toObject(UserDTO::class.java)?.let { user ->
                        users.add(user)
                    }
                }

                onChange(users)
            }
        }
    }

    override fun observeChatRooms(onChange: (List<ChatRoomDTO>) -> Unit) {
        firestore.chatRoomsCollection().addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "chatRoomsCollectionSnapshot: ", error)
                return@addSnapshotListener
            }
            value?.let {
                val rooms = mutableListOf<ChatRoomDTO>()
                for (doc in it.documents) {
                    doc.toObject(ChatRoomDTO::class.java)?.let { room ->
                        rooms.add(room)
                    }
                }

                onChange(rooms)
            }
        }
    }

    override fun observeChatMessages(
        chatRoomId: String,
        onChange: (List<ChatMessageDTO>) -> Unit
    ) {
        firestore.chatMessagesCollection(chatRoomId).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "chatMessagesCollection Snapshot: ", error)
                return@addSnapshotListener
            }
            value?.let {
                val messages = mutableListOf<ChatMessageDTO>()
                for (doc in it.documents) {
                    doc.toObject(ChatMessageDTO::class.java)?.let { message ->
                        messages.add(message)
                    }
                }

                onChange(messages)
            }
        }
    }
}