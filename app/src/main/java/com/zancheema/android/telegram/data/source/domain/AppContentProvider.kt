package com.zancheema.android.telegram.data.source.domain

import android.content.ContentResolver

interface AppContentProvider {
    fun getContactPhoneNumbers(contentResolver: ContentResolver): List<String>
}