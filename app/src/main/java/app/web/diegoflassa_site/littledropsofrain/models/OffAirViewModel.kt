package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.off_air.OffAirFragment

class OffAirViewModel(state: SavedStateHandle) : ViewModel() {

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

    private var mViewState = MutableLiveData(OffAirViewState()).apply {
        value?.text = "This is ${OffAirFragment::class.simpleName} Fragment"
    }
    val viewState: OffAirViewState
        get() :OffAirViewState {
            return savedStateHandle.get(SAVE_STATE_KEY)!!
        }
}