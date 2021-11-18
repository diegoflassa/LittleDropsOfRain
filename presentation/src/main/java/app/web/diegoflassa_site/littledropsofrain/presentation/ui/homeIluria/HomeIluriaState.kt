package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.helpers.DummyData

data class HomeIluriaState(
    var carouselItemsSpotlight: List<Product> = listOf(),
    var carouselItemsCategories: List<CategoryItem> = listOf(),
    var carouselItemsNewCollection: List<Product> = listOf(),
    var carouselItemsRecommended: List<Product> = listOf(),
){
    companion object{
        fun getDummyData() : LiveData<HomeIluriaState> {
            val ret = MutableLiveData(HomeIluriaState())
            ret.value!!.carouselItemsSpotlight = DummyData.getSpotlightCarouselItems()
            ret.value!!.carouselItemsCategories = DummyData.getCategoriesCarouselItems()
            ret.value!!.carouselItemsNewCollection = DummyData.getNewCollectionCarouselItems()
            ret.value!!.carouselItemsRecommended = DummyData.getRecommendationsCarouselItems()
            return ret
        }
    }
}