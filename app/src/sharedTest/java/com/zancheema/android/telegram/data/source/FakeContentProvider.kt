package com.zancheema.android.telegram.data.source

import android.content.ContentResolver
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.zancheema.android.telegram.data.source.AppContentProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeContentProvider @Inject constructor() : AppContentProvider {

    var contactPhoneNumbers: List<String> = emptyList()
    var loggedIn: Boolean = false
    var currentPhoneNumber: String? = null
    var navcontroller: NavController? = null

    override fun getContactPhoneNumbers(contentResolver: ContentResolver): List<String> {
        return contactPhoneNumbers
    }

    override fun isLoggedIn(): Boolean {
        return loggedIn
    }

    override fun getCurrentUserPhoneNumber(): String? {
        return currentPhoneNumber
    }

    override fun findNavController(fragment: Fragment): NavController {
        if (navcontroller == null) {
            throw Exception("Test NavController not set")
        }
        return navcontroller as NavController
    }
}