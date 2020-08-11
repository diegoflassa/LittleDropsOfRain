package io.github.diegoflassa.littledropsofrain.data

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.data.entities.Product

/**
 * This class observes the current FirebaseUser. If there is no logged in user, FirebaseUser will
 * be null.
 *
 * Note that onActive() and onInactive() will get triggered when the configuration changes (for
 * example when the device is rotated). This may be undesirable or expensive depending on the
 * nature of your LiveData object.
 */
class ProductLiveData : LiveData<Product?>() {

    // When this object has an active observer, start observing the FirebaseAuth state to see if
    // there is currently a logged in user.
    override fun onActive() {
        //
    }

    // When this object no longer has an active observer, stop observing the FirebaseAuth state to
    // prevent memory leaks.
    override fun onInactive() {
        //
    }
}