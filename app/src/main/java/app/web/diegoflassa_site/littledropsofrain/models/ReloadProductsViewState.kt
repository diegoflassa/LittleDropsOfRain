package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.LiveData

class ReloadProductsViewState : LiveData<ReloadProductsViewState>(){

    var text = ""
    var progress = StringBuffer()
    var removeNotFoundProducts = true
}
