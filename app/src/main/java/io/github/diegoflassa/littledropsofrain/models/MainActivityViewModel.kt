package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.MainActivity
import io.github.diegoflassa.littledropsofrain.auth.FirebaseAuthLiveData

class MainActivityViewModel : ViewModel() {
    private val firebaseAuthLiveData : FirebaseAuthLiveData= FirebaseAuthLiveData()

    fun getFirebaseAuthLiveData() : FirebaseAuthLiveData {
        return firebaseAuthLiveData
    }

    private val mViewState = MutableLiveData<MainActivityViewState>().apply {
        value?.text = "This is ${MainActivity::class.simpleName} Fragment"
    }
    val viewState: LiveData<MainActivityViewState> = mViewState
}