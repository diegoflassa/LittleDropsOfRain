package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData

class AllProductsFilterDialogViewState : LiveData<AllProductsFilterDialogViewState>() {

    var text: String = ""
    var categories: LinkedHashSet<String> = LinkedHashSet()
    var likes: Boolean = false

}
