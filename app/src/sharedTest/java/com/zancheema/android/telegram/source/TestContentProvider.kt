package com.zancheema.android.telegram.source

import android.content.ContentResolver
import com.zancheema.android.telegram.data.source.domain.AppContentProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestContentProvider @Inject constructor() : AppContentProvider {

    var phoneNumbers: List<String> = emptyList()

    override fun getContactPhoneNumbers(contentResolver: ContentResolver): List<String> {
        return phoneNumbers
    }
}