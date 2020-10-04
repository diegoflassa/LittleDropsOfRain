package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.auth.FirebaseAuthLiveData

@Suppress("unused")
class MainActivityViewModel : ViewModel() {
    private val mFirebaseAuthLiveData: FirebaseAuthLiveData = FirebaseAuthLiveData()

    fun getFirebaseAuthLiveData(): FirebaseAuthLiveData {
        return mFirebaseAuthLiveData
    }

    private val mViewState = MainActivityViewState().apply {
        value?.text = "This is ${MainActivity::class.simpleName} Fragment"
    }
    val viewState = mViewState
}