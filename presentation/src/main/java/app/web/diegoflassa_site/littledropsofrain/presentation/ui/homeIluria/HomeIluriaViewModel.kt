package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.helpers.DummyData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeIluriaViewModel @Inject constructor() : ViewModel() {

    init{
        getCarouselItemsSpotlight()
        getCarouselItemsCategories()
        getCarouselItemsNewCollection()
        getCarouselItemsRecommended()

    }
    private var _carouselItemsSpotlight = MutableLiveData<List<Product>>(listOf())
    val carouselItemsSpotlight: LiveData<List<Product>> = _carouselItemsSpotlight
    private var _carouselItemsCategories = MutableLiveData<List<CategoryItem>>(listOf())
    val carouselItemsCategories: LiveData<List<CategoryItem>> = _carouselItemsCategories
    private var _carouselItemsNewCollection = MutableLiveData<List<Product>>(listOf())
    val carouselItemsNewCollection: LiveData<List<Product>> = _carouselItemsNewCollection
    private var _carouselItemsRecommended = MutableLiveData<List<Product>>(listOf())
    val carouselItemsRecommended: LiveData<List<Product>> = _carouselItemsRecommended


    fun getCarouselItemsSpotlight(){
        _carouselItemsSpotlight.postValue(DummyData.getSpotlightCarouselItems())
    }

    fun getCarouselItemsCategories(){
        _carouselItemsCategories.postValue(DummyData.getCategoriesCarouselItems())
    }

    fun getCarouselItemsNewCollection(){
        _carouselItemsNewCollection.postValue(DummyData.getNewCollectionCarouselItems())
    }

    fun getCarouselItemsRecommended(){
        _carouselItemsRecommended.postValue(DummyData.getRecommendationsCarouselItems())
    }

}