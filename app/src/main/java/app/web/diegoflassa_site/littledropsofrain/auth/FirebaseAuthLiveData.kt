package app.web.diegoflassa_site.littledropsofrain.auth

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser


class FirebaseAuthLiveData : LiveData<FirebaseUser?>() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val authStateListener =
        AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            value = firebaseUser
        }

    override fun onActive() {
        super.onActive()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onInactive() {
        super.onInactive()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}