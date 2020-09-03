package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.reload_products.ReloadProductsFragment

class ReloadProductsViewModel : ViewModel() {
    private val mViewState = ReloadProductsViewState().apply {
        value?.text = "This is ${ReloadProductsFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}