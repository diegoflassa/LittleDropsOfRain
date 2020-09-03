package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.admin.AdminFragment

class AdminViewModel : ViewModel() {

    private val mViewState = AdminViewState().apply {
        value?.text = "This is ${AdminFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}