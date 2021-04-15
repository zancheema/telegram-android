package com.zancheema.android.telegram.data.source.domain

import android.content.ContentResolver
import android.provider.ContactsContract
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
}