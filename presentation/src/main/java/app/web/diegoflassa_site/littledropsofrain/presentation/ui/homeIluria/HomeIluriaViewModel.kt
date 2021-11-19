package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.data.helpers.DummyData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeIluriaViewModel @Inject constructor() : ViewModel() {

    private var _uiState = MutableLiveData(HomeIluriaState())
    val uiState: LiveData<HomeIluriaState>
        get() = _uiState

    init {
        getCarouselItemsSpotlight()
        getCarouselItemsCategories()
        getCarouselItemsNewCollection()
        getCarouselItemsRecommended()
    }

    fun getCarouselItemsSpotlight() {
        uiState.value!!.carouselItemsSpotlight = DummyData.getSpotlightCarouselItems()
    }

    fun getCarouselItemsCategories() {
        uiState.value!!.carouselItemsCategories = DummyData.getCategoriesCarouselItems()
    }

    fun getCarouselItemsNewCollection() {
        uiState.value!!.carouselItemsNewCollection = DummyData.getNewCollectionCarouselItems()
    }

    fun getCarouselItemsRecommended() {
        uiState.value!!.carouselItemsRecommended = DummyData.getRecommendationsCarouselItems()
    }

    fun refresh() {
        getCarouselItemsSpotlight()
        getCarouselItemsCategories()
        getCarouselItemsNewCollection()
        getCarouselItemsRecommended()
    }

}