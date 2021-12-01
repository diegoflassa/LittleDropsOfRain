/*
 * Copyright 2021 The Little Drops of Rain Project
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.web.diegoflassa_site.littledropsofrain.presentation.ui.homeIluria

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.helpers.DummyData
import app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
import app.web.diegoflassa_site.littledropsofrain.data.repository.CategoriesRepository
import app.web.diegoflassa_site.littledropsofrain.data.repository.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeIluriaViewModel @Inject constructor(private val productsRepository: ProductsRepository, private val categoriesRepository: CategoriesRepository) :
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

    class CarouselItemsCategoriesListener(private val viewModel: HomeIluriaViewModel) :
        OnDataChangeListener<List<CategoryItem>> {
        override fun onDataChanged(item: List<CategoryItem>) {
            val homeIluriaState = HomeIluriaState()
            homeIluriaState.copy(viewModel.uiState.value!!)
            homeIluriaState.carouselItemsCategories = item
            viewModel.uiState.postValue(homeIluriaState)
            viewModel.uiState.value!!.isRefreshingCategories.value = false
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
        categoriesRepository.loadAll(CarouselItemsCategoriesListener(this))
        uiState.value!!.isRefreshingCategories.postValue(true)
        uiState.value!!.isRefreshing.postValue(true)
        uiState.value!!.carouselItemsCategories = DummyData.getCategoriesCarouselItems()
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
