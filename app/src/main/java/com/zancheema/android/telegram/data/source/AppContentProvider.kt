package com.zancheema.android.telegram.data.source

import android.content.ContentResolver

interface AppContentProvider {
    fun getContactPhoneNumbers(contentResolver: ContentResolver): List<String>

    fun isLoggedIn(): Boolean
}