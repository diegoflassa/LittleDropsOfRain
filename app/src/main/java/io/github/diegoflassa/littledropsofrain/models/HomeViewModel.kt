package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.ViewModel
import io.github.diegoflassa.littledropsofrain.fragments.ProductsFilters
import io.github.diegoflassa.littledropsofrain.ui.home.HomeFragment

class HomeViewModel : ViewModel() {

    var filters: ProductsFilters = ProductsFilters.default

    private val mViewState = HomeViewState().apply {
        value?.text = "This is ${HomeFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}