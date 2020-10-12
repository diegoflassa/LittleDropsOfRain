package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.off_air.OffAirFragment

class OffAirViewModel : ViewModel() {

    private val mViewState = OffAirViewState().apply {
        value?.text = "This is ${OffAirFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}