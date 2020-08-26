package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.ui.reload_products.ReloadProductsFragment

class ReloadProductsViewModel : ViewModel() {
    private val mViewState = ReloadProductsViewState().apply {
        value?.text = "This is ${ReloadProductsFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}