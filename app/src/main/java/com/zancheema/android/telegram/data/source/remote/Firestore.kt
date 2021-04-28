package com.zancheema.android.telegram.data.source.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zancheema.android.telegram.data.source.AppContentProvider
import javax.inject.Inject

class Firestore @Inject constructor(
    val contentProvider: AppContentProvider
) {
    fun usersCollection() = Firebase.firestore.collection("users")

    fun currentUserDoc() = usersCollection()
        .document(contentProvider.getCurrentUserPhoneNumber() ?: "")

    fun chatRoomsCollection() = currentUserDoc()
        .collection("chatRooms")

    fun chatRoomsCollection(phoneNumber: String) = usersCollection()
        .document(phoneNumber)
        .collection("chatRooms")

    fun chatMessagesCollection(chatRoomId: String) = chatRoomsCollection()
        .document(chatRoomId)
        .collection("chatMessages")

    fun chatMessagesCollection(chatRoomId: String, phoneNumber: String) =
        chatRoomsCollection(phoneNumber)
            .document(chatRoomId)
            .collection("chatMessages")
}