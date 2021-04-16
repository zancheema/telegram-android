package com.zancheema.android.telegram.source

import android.content.ContentResolver
import com.zancheema.android.telegram.data.source.AppContentProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeContentProvider @Inject constructor() : AppContentProvider {

    var phoneNumbers: List<String> = emptyList()
    var loggedIn: Boolean = false
    var phoneNumber: String? = null

    override fun getContactPhoneNumbers(contentResolver: ContentResolver): List<String> {
        return phoneNumbers
    }

    override fun isLoggedIn(): Boolean {
        return loggedIn
    }

    override fun getCurrentUserPhoneNumber(): String? {
        return phoneNumber
    }
}