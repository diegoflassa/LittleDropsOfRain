package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.fragments.ProductsFilters

class MyLikedProductsViewState : LiveData<MyLikedProductsViewState>() {

    var text: String = ""
    var filters: ProductsFilters = ProductsFilters.default

}
