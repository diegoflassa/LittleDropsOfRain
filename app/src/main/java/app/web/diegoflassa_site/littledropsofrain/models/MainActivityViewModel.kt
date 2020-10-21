package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.MainActivity
import app.web.diegoflassa_site.littledropsofrain.auth.FirebaseAuthLiveData

@Suppress("UNUSED")
class MainActivityViewModel(state : SavedStateHandle) : ViewModel() {
    companion object {
        private const val SAVE_STATE_KEY_FIREBASE_AUTH = "SAVE_STATE_KEY_FIREBASE_AUTH"
        private const val SAVE_STATE_KEY = "SAVE_STATE_KEY"
    }

    private var mFirebaseAuthLiveData: FirebaseAuthLiveData
        get() :FirebaseAuthLiveData {
            return savedStateHandle.get(SAVE_STATE_KEY_FIREBASE_AUTH)!!
        }
    private val savedStateHandle = state
    private fun saveState() {
        // Sets a new value for the object associated to the key.
        savedStateHandle.set(SAVE_STATE_KEY, mViewState)
        savedStateHandle.set(SAVE_STATE_KEY_FIREBASE_AUTH, mFirebaseAuthLiveData)
    }
    private var mViewState = MutableLiveData(MainActivityViewState()).apply {
        value?.text = "This is ${MainActivity::class.simpleName} Fragment"
    }
    val viewState: MainActivityViewState
        get() :MainActivityViewState {
            return savedStateHandle.get(SAVE_STATE_KEY)!!
        }

    fun getFirebaseAuthLiveData(): FirebaseAuthLiveData {
        return mFirebaseAuthLiveData
    }

    init{
        saveState()
        mFirebaseAuthLiveData = FirebaseAuthLiveData()
    }

}