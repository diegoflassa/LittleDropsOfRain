package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.messages.MessagesFragment

class MessagesViewModel(state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val SAVE_STATE_KEY = "SAVE_STATE_KEY"
    }


    init {
        saveState()
    }

    private val savedStateHandle = state
    private fun saveState() {
        // Sets a new value for the object associated to the key.
        savedStateHandle.set(SAVE_STATE_KEY, mViewState)
    }

    private var mViewState = MutableLiveData(MyLikedProductsViewState()).apply {
        value?.text = "This is ${MessagesFragment::class.simpleName} Fragment"
    }
    val viewState: MessagesViewState
        get() :MessagesViewState {
            return savedStateHandle.get(SAVE_STATE_KEY)!!
        }
}