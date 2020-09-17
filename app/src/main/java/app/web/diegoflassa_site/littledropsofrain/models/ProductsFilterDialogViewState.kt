package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData

class ProductsFilterDialogViewState : LiveData<ProductsFilterDialogViewState>(){

    var text : String = ""
    var categories: LinkedHashSet<String> = LinkedHashSet()

}
