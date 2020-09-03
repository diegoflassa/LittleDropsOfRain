package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.home.HomeFragment

class HomeViewModel : ViewModel() {

    private val mViewState = HomeViewState().apply {
        value?.text = "This is ${HomeFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}