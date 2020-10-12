package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.fragments.AllProductsFilterDialogFragment

class AllProductsFilterDialogViewModel : ViewModel() {

    private val mViewState = AllProductsFilterDialogViewState().apply {
        value?.text = "This is ${AllProductsFilterDialogFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}