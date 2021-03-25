package com.zancheema.android.telegram.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.LiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * This class observes the current FirebaseUser. If there is no logged in user, FirebaseUser will
 * be null.
 *
 * Note that onActive() and onInactive() will get triggered when the configuration changes (for
 * example when the device is rotated). This may be undesirable or expensive depending on the
 * nature of your LiveData object, but is okay for this purpose since we are only adding and
 * removing the authStateListener.
 */
class FirebaseUserLiveData : LiveData<FirebaseUser?>() {
    private val auth = Firebase.auth

    /**
     * [getValue] of this [LiveData] is specified by [FirebaseAuth.AuthStateListener]
     */
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        value = firebaseAuth.currentUser
    }

    /**
     * When this object has an active observer, start observing the FirebaseAuth state to see if
     * there is currently a logged in user.
     */
    override fun onActive() {
        auth.addAuthStateListener(authStateListener)
    }

    /**
     * When this object no longer has an active observer, stop observing the FirebaseAuth state to
     * prevent memory leaks.
     */
    override fun onInactive() {
        auth.removeAuthStateListener(authStateListener)
    }
}