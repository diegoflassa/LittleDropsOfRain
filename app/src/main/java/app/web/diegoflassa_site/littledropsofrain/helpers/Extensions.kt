package app.web.diegoflassa_site.littledropsofrain.helpers

import androidx.fragment.app.Fragment

fun Fragment?.runOnUiThread(action: () -> Unit) {
    this ?: return
    if (!isAdded) return // Fragment not attached to an Activity
    activity?.runOnUiThread(action)
}

fun Fragment?.isSafeToAccessViewModel(): Boolean {
    this ?: return false
    return (!isRemoving && !isDetached && isAdded)
}