package io.github.diegoflassa.littledropsofrain.models

import androidx.lifecycle.LiveData
import io.github.diegoflassa.littledropsofrain.fragments.ProductsFilters

class HomeViewState : LiveData<HomeViewState>(){

    var text : String = ""
    var filters: ProductsFilters = ProductsFilters.default

}
