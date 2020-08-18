package io.github.diegoflassa.littledropsofrain

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.auth.FirebaseAuthLiveData

class MainActivityViewModel : ViewModel() {
    private val firebaseAuthLiveData : FirebaseAuthLiveData= FirebaseAuthLiveData()

    fun getFirebaseAuthLiveData() : FirebaseAuthLiveData {
        return firebaseAuthLiveData
    }
}