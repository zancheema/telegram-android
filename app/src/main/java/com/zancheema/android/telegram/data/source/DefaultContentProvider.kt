package com.zancheema.android.telegram.data.source

import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultContentProvider @Inject constructor() : AppContentProvider {
    override fun getContactPhoneNumbers(contentResolver: ContentResolver) : List<String> {
        val numbers = mutableListOf<String>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.count > 0) {
            for (i in 0 until cursor.count) {
                cursor.moveToPosition(i)
                val phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                numbers.add(phoneNumber)
            }
        }
        cursor?.close()

        return numbers
    }

    override fun getCurrentUserPhoneNumber(): String? {
        return Firebase.auth.currentUser?.phoneNumber
    }

    override fun findNavController(fragment: Fragment): NavController {
        return NavHostFragment.findNavController(fragment)
    }

    override fun isLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }
}