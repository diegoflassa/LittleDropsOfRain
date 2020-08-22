package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.MainActivity
import io.github.diegoflassa.littledropsofrain.auth.FirebaseAuthLiveData

class MainActivityViewModel : ViewModel() {
    private val mFirebaseAuthLiveData : FirebaseAuthLiveData= FirebaseAuthLiveData()

    fun getFirebaseAuthLiveData() : FirebaseAuthLiveData {
        return mFirebaseAuthLiveData
    }

    private val mViewState = MainActivityViewState().apply {
        value?.text = "This is ${MainActivity::class.simpleName} Fragment"
    }
    val viewState = mViewState
}