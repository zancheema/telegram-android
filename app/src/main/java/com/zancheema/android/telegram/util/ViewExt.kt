package com.zancheema.android.telegram.util

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.EventObserver

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(text: String, duration: Int) {
    Snackbar.make(this, text, duration).show()
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setUpSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    duration: Int
) {
    snackbarEvent.observe(lifecycleOwner, EventObserver { resId ->
        showSnackbar(context.getString(resId), duration)
    })
}

/**
 * Triggers a nullable snackbar message
 */
fun View.setUpSnackbarNullable(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>?>,
    duration: Int
) {
    snackbarEvent.observe(lifecycleOwner, EventObserver { resId ->
        showSnackbar(context.getString(resId), duration)
    })
}