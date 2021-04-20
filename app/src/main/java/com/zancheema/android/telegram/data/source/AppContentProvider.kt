package com.zancheema.android.telegram.data.source

import android.content.ContentResolver
import androidx.fragment.app.Fragment
import androidx.navigation.NavController

interface AppContentProvider {
    fun getContactPhoneNumbers(contentResolver: ContentResolver): List<String>

    fun isLoggedIn(): Boolean

    fun getCurrentUserPhoneNumber(): String?

    fun findNavController(fragment: Fragment): NavController
}