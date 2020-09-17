package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.fragments.ProductsFilterDialogFragment

class ProductsFilterDialogViewModel : ViewModel() {

    private val mViewState = ProductsFilterDialogViewState().apply {
        value?.text = "This is ${ProductsFilterDialogFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}