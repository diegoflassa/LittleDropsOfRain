package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.iluria.IluriaFragment

class IluriaViewModel : ViewModel() {

    private val mViewState = MutableLiveData<IluriaViewState>().apply {
        value?.text = "This is ${IluriaFragment::class.simpleName} Fragment"
    }
    val viewState: LiveData<IluriaViewState> = mViewState
}