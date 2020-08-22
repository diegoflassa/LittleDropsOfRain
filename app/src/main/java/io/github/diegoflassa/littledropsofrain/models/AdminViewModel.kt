package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.admin.AdminFragment

class AdminViewModel : ViewModel() {

    private val mViewState = AdminViewState().apply {
        value?.text = "This is ${AdminFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}