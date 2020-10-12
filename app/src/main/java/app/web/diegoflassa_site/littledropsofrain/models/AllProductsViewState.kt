package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData
import app.web.diegoflassa_site.littledropsofrain.fragments.AllProductsFilters

class AllProductsViewState : LiveData<AllProductsViewState>() {

    var text: String = ""
    var filters: AllProductsFilters = AllProductsFilters.default

}
