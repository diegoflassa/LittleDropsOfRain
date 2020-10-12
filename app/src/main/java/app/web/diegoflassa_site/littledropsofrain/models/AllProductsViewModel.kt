package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.all_products.AllProductsFragment

class AllProductsViewModel : ViewModel() {

    private val mViewState = AllProductsViewState().apply {
        value?.text = "This is ${AllProductsFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}