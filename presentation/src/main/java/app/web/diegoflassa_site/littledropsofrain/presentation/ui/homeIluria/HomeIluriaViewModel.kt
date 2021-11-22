package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.helpers.DummyData
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeIluriaViewModel @Inject constructor(private val productsRepository: ProductsRepository) :
    ViewModel() {
    var uiState = MutableLiveData(HomeIluriaState())

    class CarouselItemsSpotlightListener(private val viewModel: HomeIluriaViewModel) :
        OnDataChangeListener<List<Product>> {
        override fun onDataChanged(item: List<Product>) {
            val homeIluriaState = HomeIluriaState()
            homeIluriaState.copy(viewModel.uiState.value!!)
            homeIluriaState.carouselItemsSpotlight = item
            viewModel.uiState.postValue(homeIluriaState)
            viewModel.uiState.value!!.isRefreshingSpotlight.value = false
            viewModel.uiState.value!!.isRefreshing.postValue(viewModel.uiState.value!!.isRefreshingSpotlight.value!! || viewModel.uiState.value!!.isRefreshingCategories.value!! || viewModel.uiState.value!!.isRefreshingNewCollection.value!! || viewModel.uiState.value!!.isRefreshingRecommended.value!!)
        }
    }

    private fun getCarouselItemsSpotlight() {
        productsRepository.loadAll(CarouselItemsSpotlightListener(this))
        uiState.value!!.isRefreshingSpotlight.postValue(true)
        uiState.value!!.isRefreshing.postValue(true)
        uiState.value!!.carouselItemsSpotlight = DummyData.getSpotlightCarouselItems()
    }

    private fun getCarouselItemsCategories() {
        uiState.value!!.isRefreshingCategories.postValue(true)
        uiState.value!!.carouselItemsCategories = DummyData.getCategoriesCarouselItems()
        uiState.value!!.isRefreshingCategories.postValue(false)
    }

    private fun getCarouselItemsNewCollection() {
        uiState.value!!.isRefreshingNewCollection.postValue(true)
        uiState.value!!.carouselItemsNewCollection = DummyData.getNewCollectionCarouselItems()
        uiState.value!!.isRefreshingNewCollection.postValue(false)
    }

    private fun getCarouselItemsRecommended() {
        uiState.value!!.isRefreshingRecommended.postValue(true)
        uiState.value!!.carouselItemsRecommended = DummyData.getRecommendationsCarouselItems()
        uiState.value!!.isRefreshingRecommended.postValue(false)
    }

    fun refresh() {
        uiState.value!!.isRefreshing.postValue(true)
        getCarouselItemsSpotlight()
        getCarouselItemsCategories()
        getCarouselItemsNewCollection()
        getCarouselItemsRecommended()
    }

}