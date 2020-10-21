package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData

class ReloadProductsViewState : LiveData<ReloadProductsViewState>() {

    var text = ""
    var progress = StringBuilder()
    var removeNotFoundProducts = false
    var unpublishNotFoundProducts = true
}
