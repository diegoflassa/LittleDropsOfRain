package app.web.diegoflassa_site.littledropsofrain.models

import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.ui.my_liked_products.MyLikedProductsFragment

class MyLikedProductsViewModel : ViewModel() {

    private val mViewState = MyLikedProductsViewState().apply {
        value?.text = "This is ${MyLikedProductsFragment::class.simpleName} Fragment"
    }
    val viewState = mViewState
}