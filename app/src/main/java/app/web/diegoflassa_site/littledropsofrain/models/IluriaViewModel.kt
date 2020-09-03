package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.iluria.IluriaFragment

class IluriaViewModel : ViewModel() {

    private val mViewState = IluriaViewState().apply {
        value?.text = "This is ${IluriaFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}